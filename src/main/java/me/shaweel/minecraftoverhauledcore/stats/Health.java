package me.shaweel.minecraftoverhauledcore.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.shaweel.minecraftoverhauledcore.Utils;
import me.shaweel.minecraftoverhauledcore.network.HealthSyncPayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;

public class Health {
	public static final HashMap<EntityType<?>, Integer> baseHealth = new HashMap<>(Map.ofEntries(
		Map.entry(EntityTypes.PLAYER, 50),
		Map.entry(EntityTypes.ZOMBIE, 50),
		Map.entry(EntityTypes.SKELETON, 50),
		Map.entry(EntityTypes.CREEPER, 50),
		Map.entry(EntityTypes.SPIDER, 50),
		Map.entry(EntityTypes.PIGLIN, 250),
		Map.entry(EntityTypes.CAVE_SPIDER, 100),
		Map.entry(EntityTypes.POLAR_BEAR, 30),
		Map.entry(EntityTypes.COW, 30),
		Map.entry(EntityTypes.PIG, 30),
		Map.entry(EntityTypes.RABBIT, 30),
		Map.entry(EntityTypes.SHEEP, 30),
		Map.entry(EntityTypes.CHICKEN, 30)
	));

	public static final HashMap<UUID, Integer> maxHealthMap = new HashMap<>();
	public static final HashMap<UUID, Integer> currentHealthMap = new HashMap<>();

	private static boolean isInitialized(UUID entity) {
		return currentHealthMap.containsKey(entity) && maxHealthMap.containsKey(entity);
	}

	public static void setCurrentHealth(UUID entity, int health) {
		Entity actualEntity = Utils.getEntity(entity);

		if (!isInitialized(entity)) {
			System.err.printf("Attempted to set the current health of a UUID which isn't present in currentHealthMap and/or maxHealthMap");
			Thread.dumpStack();
			return;
		}

		currentHealthMap.put(entity, health);

		if (actualEntity instanceof ServerPlayer player) {
			sendHealthSyncPacket(player);
		}
	}

	public static int getCurrentHealth(UUID entity) {
		if (!isInitialized(entity)) {
			System.err.printf("Attempted to get the current health of a UUID which isn't present in currentHealthMap and/or maxHealthMap");
			Thread.dumpStack();
			return -1;
		}

		return currentHealthMap.get(entity);
	}

	public static void setMaxHealth(UUID entity, int health) {
		Entity actualEntity = Utils.getEntity(entity);

		if (!isInitialized(entity)) {
			System.err.printf("Attempted to set the max health of a UUID which isn't present in currentHealthMap and/or maxHealthMap");
			Thread.dumpStack();
			return;
		}

		maxHealthMap.put(entity, health);

		if (actualEntity instanceof ServerPlayer player) {
			sendHealthSyncPacket(player);
		}
	}

	public static int getMaxHealth(UUID entity) {
		if (!isInitialized(entity)) {
			System.err.printf("Attempted to get the max health of a UUID which isn't present in currentHealthMap and/or maxHealthMap");
			Thread.dumpStack();
			return -1;
		}

		return maxHealthMap.get(entity);
	}

	private static void discardIfInvalid(Entity entity) {
		if (entity instanceof Mob mob && !baseHealth.containsKey(mob.getType())) {
			mob.discard();
		}
	}

	private static void putBaseHealthValues(Entity entity) {
		if (!maxHealthMap.containsKey(entity.getUUID())) {
			maxHealthMap.put(entity.getUUID(), baseHealth.get(entity.getType()));
		}

		if (!currentHealthMap.containsKey(entity.getUUID())) {
			currentHealthMap.put(entity.getUUID(), baseHealth.get(entity.getType()));
		}
	}

	private static void sendHealthSyncPacketIfPlayer(Entity entity, ServerLevel world) {
		if (entity instanceof ServerPlayer player) {
			world.getServer().execute(() -> sendHealthSyncPacket(player));
		}
	}

	public static void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			discardIfInvalid(entity);
			putBaseHealthValues(entity);
			sendHealthSyncPacketIfPlayer(entity, world);
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, _) -> {
			UUID uuid = newPlayer.getUUID();

			setCurrentHealth(uuid, getMaxHealth(uuid));
		});
	}

	private static void sendHealthSyncPacket(ServerPlayer player) {
		UUID uuid = player.getUUID();
		
		ServerPlayNetworking.send(player, new HealthSyncPayload(getCurrentHealth(uuid), getMaxHealth(uuid)));
	}

	public static void hurt(UUID victim, UUID attacker) {
		hurt(victim, Damage.getDamage(attacker));
		AttackCooldown.markAttack(attacker);
	}

	public static void hurt(UUID victim, int damage) {
		int newCurrentHealth = getCurrentHealth(victim) - damage;
		
		if (newCurrentHealth <= 0) {
			Utils.killEntity(victim);
			setCurrentHealth(victim, 0);
			return;
		}

		setCurrentHealth(victim, newCurrentHealth);
	}

	public static void heal(UUID uuid, int amount) {
		int newCurrentHealth = Math.min(
			getCurrentHealth(uuid) + amount, 
			getMaxHealth(uuid)
		);

		setCurrentHealth(uuid, newCurrentHealth);
	}
}
