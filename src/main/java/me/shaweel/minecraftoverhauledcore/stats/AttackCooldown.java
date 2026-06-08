package me.shaweel.minecraftoverhauledcore.stats;

import java.util.HashMap;
import java.util.UUID;

import me.shaweel.minecraftoverhauledcore.network.AttackCooldownSyncPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;

public class AttackCooldown {
	//TODO: refactor to send lstAttackTime and attackCooldown whenever the player attacks instead to save server resources
	//TODO 2: listen for the sync packet on the client
	public static final int base = 2500;

	public static final HashMap<UUID, Integer> attackCooldownMap = new HashMap<>();
	public static final HashMap<UUID, Long> lastAttackTimeMap = new HashMap<>();

	public static void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity.getType() != EntityType.PLAYER) return;
			
			if (!attackCooldownMap.containsKey(entity.getUUID())) {
				attackCooldownMap.put(entity.getUUID(), base);
			}

			if (!lastAttackTimeMap.containsKey(entity.getUUID())) {
				lastAttackTimeMap.put(entity.getUUID(), 0l);
			}
		});
		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				sendAttackCooldownSyncPacket(player);
			}
		});
	}

	private static void sendAttackCooldownSyncPacket(ServerPlayer player) {
		if (!(lastAttackTimeMap.containsKey(player.getUUID()) && attackCooldownMap.containsKey(player.getUUID()))) return;

		int attackCooldown = attackCooldownMap.get(player.getUUID());
		float progress = getCooldownProgress(player.getUUID());
		ServerPlayNetworking.send(player, new AttackCooldownSyncPayload(attackCooldown, progress));
	}

	public static float getCooldownProgress(UUID player) {
		if (!(lastAttackTimeMap.containsKey(player) && attackCooldownMap.containsKey(player))) return 1;

		float elapsed = System.currentTimeMillis() - lastAttackTimeMap.get(player);
		float cooldown = (float) attackCooldownMap.get(player);
		return Math.min(elapsed / cooldown, 1.0f);
	}

	public static void markAttack(UUID attacker) {
		if (!lastAttackTimeMap.containsKey(attacker)) return;
		lastAttackTimeMap.put(attacker, System.currentTimeMillis());
	}
}
