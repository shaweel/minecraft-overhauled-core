package me.shaweel.minecraftoverhauledcore.client.stats;

import me.shaweel.minecraftoverhauledcore.network.HealthSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ClientHealth {
	public static int currentHealth;
	public static int health;

	public static void initialize() {
		PayloadTypeRegistry.clientboundPlay().register(HealthSyncPayload.TYPE, HealthSyncPayload.CODEC);

		ClientPlayNetworking.registerGlobalReceiver(HealthSyncPayload.TYPE, (payload, context) -> {
			currentHealth = payload.currentHealth();
			health = payload.health();
		});
	}
}
