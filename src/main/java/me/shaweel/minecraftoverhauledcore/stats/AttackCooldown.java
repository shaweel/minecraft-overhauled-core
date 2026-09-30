package me.shaweel.minecraftoverhauledcore.stats;

import java.util.HashMap;
import java.util.UUID;

import me.shaweel.minecraftoverhauledcore.Utils;
import me.shaweel.minecraftoverhauledcore.network.AttackCooldownSyncPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;

public class AttackCooldown {
	public static final int base = 1500;

	public static final HashMap<UUID, Integer> attackCooldownMap = new HashMap<>();
	public static final HashMap<UUID, Long> lastAttackTimeMap = new HashMap<>();

	private static void putBaseAttackCooldownValues(Entity entity) {
		if (!attackCooldownMap.containsKey(entity.getUUID())) {
			attackCooldownMap.put(entity.getUUID(), base);
		}

		if (!lastAttackTimeMap.containsKey(entity.getUUID())) {
			lastAttackTimeMap.put(entity.getUUID(), 0l);
		}
	}

	public static void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity.getType() != EntityTypes.PLAYER) return;
			
			putBaseAttackCooldownValues(entity);
		});
	}

	private static boolean isInitialized(UUID entity) {
		return attackCooldownMap.containsKey(entity) && lastAttackTimeMap.containsKey(entity);
	}

	public static void setAttackCooldown(UUID entity, int attackCooldown) {
		Entity actualEntity = Utils.getEntity(entity);

		if (!isInitialized(entity)) {
			System.err.printf("Attempted to set the current health of a UUID which isn't present in attackCooldownMap and/or lastAttackTimeMap");
			Thread.dumpStack();
			return;
		}

		attackCooldownMap.put(entity, attackCooldown);

		if (actualEntity instanceof ServerPlayer player) {
			sendAttackCooldownSyncPacket(player);
		}
	}

	public static int getAttackCooldown(UUID entity) {
		if (!isInitialized(entity)) {
			System.err.printf("Attempted to get the attack cooldown of a UUID which isn't present in attackCooldownMap and/or lastAttackTimeMap");
			Thread.dumpStack();
			return -1;
		}

		return attackCooldownMap.get(entity);
	}

	public static void setLastAttackTime(UUID entity, long lastAttackTime) {
		Entity actualEntity = Utils.getEntity(entity);

		if (!isInitialized(entity)) {
			System.err.printf("Attempted to set the last attack time of a UUID which isn't present in attackCooldownMap and/or lastAttackTimeMap");
			Thread.dumpStack();
			return;
		}

		lastAttackTimeMap.put(entity, lastAttackTime);

		if (actualEntity instanceof ServerPlayer player) {
			sendAttackCooldownSyncPacket(player);
		}
	}

	public static long getLastAttackTime(UUID entity) {
		if (!isInitialized(entity)) {
			System.err.printf("Attempted to get the last attack time of a UUID which isn't present in attackCooldownMap and/or lastAttackTimeMap");
			Thread.dumpStack();
			return -1;
		}

		return lastAttackTimeMap.get(entity);
	}

	private static void sendAttackCooldownSyncPacket(ServerPlayer player) {
		UUID uuid = player.getUUID();

		ServerPlayNetworking.send(player, new AttackCooldownSyncPayload(getAttackCooldown(uuid), getCooldownProgress(uuid)));
	}

	public static float getCooldownProgress(UUID player) {
		float elapsed = System.currentTimeMillis() - getLastAttackTime(player);
		float cooldown = (float) getAttackCooldown(player);

		return Math.min(elapsed / cooldown, 1.0f);
	}

	public static void markAttack(UUID attacker) {
		if (Utils.getEntity(attacker) instanceof Player) {
			setLastAttackTime(attacker, System.currentTimeMillis());
		}
	}
}
