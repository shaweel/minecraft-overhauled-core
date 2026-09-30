package me.shaweel.minecraftoverhauledcore.client.stats;

import me.shaweel.minecraftoverhauledcore.network.AttackCooldownSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ClientAttackCooldown {
	public static int attackCooldown;
	public static float attackCooldownProgress;

	public static void initialize() {
		PayloadTypeRegistry.clientboundPlay().register(AttackCooldownSyncPayload.TYPE, AttackCooldownSyncPayload.CODEC);

		ClientPlayNetworking.registerGlobalReceiver(AttackCooldownSyncPayload.TYPE, (payload, context) -> {
			attackCooldown = payload.attackCooldown();
			attackCooldownProgress = payload.attackCooldownProgress();
		});
	}
}
