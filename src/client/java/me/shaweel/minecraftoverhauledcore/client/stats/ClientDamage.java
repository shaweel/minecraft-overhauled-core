package me.shaweel.minecraftoverhauledcore.client.stats;

import me.shaweel.minecraftoverhauledcore.network.DamageSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ClientDamage {
	public static int damage;

	public static void initialize() {
		PayloadTypeRegistry.clientboundPlay().register(DamageSyncPayload.TYPE, DamageSyncPayload.CODEC);

		ClientPlayNetworking.registerGlobalReceiver(DamageSyncPayload.TYPE, (payload, context) -> {
			damage = payload.damage();
		});
	}
}
