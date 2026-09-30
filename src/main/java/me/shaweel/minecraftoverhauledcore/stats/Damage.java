package me.shaweel.minecraftoverhauledcore.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.shaweel.minecraftoverhauledcore.Utils;
import me.shaweel.minecraftoverhauledcore.network.DamageSyncPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

public class Damage {
	public static final HashMap<EntityType<?>, Integer> baseDamage = new HashMap<>(Map.ofEntries(
		Map.entry(EntityTypes.PLAYER, 3),
		Map.entry(EntityTypes.ZOMBIE, 30),
		Map.entry(EntityTypes.SKELETON, 30),
		Map.entry(EntityTypes.CREEPER, 0),
		Map.entry(EntityTypes.SPIDER, 30),
		Map.entry(EntityTypes.PIGLIN, 100),
		Map.entry(EntityTypes.CAVE_SPIDER, 50),
		Map.entry(EntityTypes.POLAR_BEAR, 0),
		Map.entry(EntityTypes.COW, 0),
		Map.entry(EntityTypes.PIG, 0),
		Map.entry(EntityTypes.RABBIT, 0),
		Map.entry(EntityTypes.SHEEP, 0),
		Map.entry(EntityTypes.CHICKEN, 0)
	));

	public static final HashMap<UUID, Integer> damageMap = new HashMap<>();

	private static boolean isInitialized(UUID entity) {
		return damageMap.containsKey(entity);
	}

	public static void setDamage(UUID entity, int damage) {
		Entity actualEntity = Utils.getEntity(entity);

		if (!isInitialized(entity)) {
			System.err.printf("Attempted to set thedamage of a UUID which isn't present in damageMap");
			Thread.dumpStack();
			return;
		}

		damageMap.put(entity, damage);

		if (actualEntity instanceof ServerPlayer player) {
			sendDamageSyncPacket(player);
		}
	}

	public static int getDamage(UUID entity) {
		if (!isInitialized(entity)) {
			System.err.printf("Attempted to get the damage of a UUID which isn't present in damageMap");
			Thread.dumpStack();
			return -1;
		}

		return damageMap.get(entity);
	}

	private static void sendDamageSyncPacket(ServerPlayer player) {
		UUID uuid = player.getUUID();
		
		ServerPlayNetworking.send(player, new DamageSyncPayload(getDamage(uuid)));
	}

	public static void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (!baseDamage.containsKey(entity.getType())) return;
			
			if (!damageMap.containsKey(entity.getUUID())) {
				damageMap.put(entity.getUUID(), baseDamage.get(entity.getType()));
			}
		});
	}
}
