package me.shaweel.minecraftoverhauledcore.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record HealthSyncPayload(int currentHealth, int health) implements CustomPacketPayload {
	public static final Type<HealthSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("minecraft_overhauled_core", "health_sync"));
	
	public static final StreamCodec<FriendlyByteBuf, HealthSyncPayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, HealthSyncPayload::currentHealth,
		ByteBufCodecs.INT, HealthSyncPayload::health,
		HealthSyncPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
