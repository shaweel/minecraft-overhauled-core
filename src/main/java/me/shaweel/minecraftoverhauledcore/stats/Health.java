package me.shaweel.minecraftoverhauledcore.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.shaweel.minecraftoverhauledcore.Server;
import me.shaweel.minecraftoverhauledcore.network.HealthSyncPayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public class Health {
	public static final HashMap<EntityType<?>, Integer> baseHealth = new HashMap<>(Map.ofEntries(
		Map.entry(EntityType.PLAYER, 50),
		Map.entry(EntityType.ZOMBIE, 50),
		Map.entry(EntityType.SKELETON, 50),
		Map.entry(EntityType.CREEPER, 50),
		Map.entry(EntityType.SPIDER, 50),
		Map.entry(EntityType.PIGLIN, 250),
		Map.entry(EntityType.CAVE_SPIDER, 100),
		Map.entry(EntityType.POLAR_BEAR, 30),
		Map.entry(EntityType.COW, 30),
		Map.entry(EntityType.PIG, 30),
		Map.entry(EntityType.RABBIT, 30),
		Map.entry(EntityType.SHEEP, 30),
		Map.entry(EntityType.CHICKEN, 30)
	));

	public static final HashMap<UUID, Integer> healthMap = new HashMap<>();
	public static final HashMap<UUID, Integer> currentHealthMap = new HashMap<>();

	public static void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof Mob mob && !baseHealth.containsKey(mob.getType())) {
				mob.discard();
			}

			if (!healthMap.containsKey(entity.getUUID())) {
				healthMap.put(entity.getUUID(), baseHealth.get(entity.getType()));
			}

			if (!currentHealthMap.containsKey(entity.getUUID())) {
				currentHealthMap.put(entity.getUUID(), baseHealth.get(entity.getType()));
			}

			if (entity instanceof ServerPlayer player && world instanceof ServerLevel level) {
				level.getServer().execute(() -> sendHealthSyncPacket(player));
			}
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			UUID uuid = newPlayer.getUUID();
			if (!(currentHealthMap.containsKey(uuid) && healthMap.containsKey(uuid))) return;
			currentHealthMap.put(uuid, healthMap.get(uuid));
			sendHealthSyncPacket(newPlayer);
		});
	}

	private static void sendHealthSyncPacket(ServerPlayer player) {
		if (!(currentHealthMap.containsKey(player.getUUID()) && healthMap.containsKey(player.getUUID()))) return;
		
		int currentHealth = currentHealthMap.get(player.getUUID());
		int health = healthMap.get(player.getUUID());
		ServerPlayNetworking.send(player, new HealthSyncPayload(currentHealth, health));
	}

	public static void hurt(UUID victim, UUID attacker) {
		if (!(Damage.damageMap.containsKey(attacker) && currentHealthMap.containsKey(victim))) return;

		int damage = Damage.damageMap.get(attacker);
		int newCurrentHealth = currentHealthMap.get(victim) - damage;
		if (newCurrentHealth <= 0) {
			ServerPlayer[] player = {null};

			Server.server.getAllLevels().forEach(level -> {
				Entity entity = level.getEntity(victim);
				if (entity != null) entity.kill(level);
				if (entity instanceof ServerPlayer) player[0] = (ServerPlayer)entity;
			});

			currentHealthMap.put(victim, 0);
			if (player[0] != null) sendHealthSyncPacket(player[0]);
			return;
		}

		currentHealthMap.put(victim, newCurrentHealth);

		ServerPlayer[] player = {null};

		Server.server.getAllLevels().forEach(level -> {
			Entity entity = level.getEntity(victim);
			if (entity instanceof ServerPlayer) player[0] = (ServerPlayer)entity;
		});

		if (player[0] != null) sendHealthSyncPacket(player[0]);
	}

	public static void hurt(UUID victim, int damage) {
		if (!currentHealthMap.containsKey(victim)) return;

		int newCurrentHealth = currentHealthMap.get(victim) - damage;
		if (newCurrentHealth <= 0) {
			ServerPlayer[] player = {null};

			Server.server.getAllLevels().forEach(level -> {
				Entity entity = level.getEntity(victim);
				if (entity != null) entity.kill(level);
				if (entity instanceof ServerPlayer) player[0] = (ServerPlayer)entity;
			});
			
			currentHealthMap.put(victim, 0);
			if (player[0] != null) sendHealthSyncPacket(player[0]);
			return;
		}

		currentHealthMap.put(victim, newCurrentHealth);

		ServerPlayer[] player = {null};

		Server.server.getAllLevels().forEach(level -> {
			Entity entity = level.getEntity(victim);
			if (entity instanceof ServerPlayer) player[0] = (ServerPlayer)entity;
		});

		if (player[0] != null) sendHealthSyncPacket(player[0]);
	}

	public static void heal(UUID uuid, int amount) {
		if (!(currentHealthMap.containsKey(uuid) && healthMap.containsKey(uuid))) return;
		int newCurrentHealth = Math.min(currentHealthMap.get(uuid) + amount, healthMap.get(uuid));
		currentHealthMap.put(uuid, newCurrentHealth);

		ServerPlayer[] player = {null};

		Server.server.getAllLevels().forEach(level -> {
			Entity entity = level.getEntity(uuid);
			if (entity instanceof ServerPlayer) player[0] = (ServerPlayer)entity;
		});

		if (player[0] != null) sendHealthSyncPacket(player[0]);
	}
}
