package me.shaweel.minecraftoverhauledcore.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record StatSyncPayloadInt(String statName, int value) implements CustomPacketPayload {
	public static final Type<StatSyncPayloadInt> TYPE = new Type<>(Identifier.fromNamespaceAndPath("minecraft_overhauled_core", "stat_sync_long"));
	
	public static final StreamCodec<FriendlyByteBuf, StatSyncPayloadInt> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, StatSyncPayloadInt::statName,
		ByteBufCodecs.INT, StatSyncPayloadInt::value,
		StatSyncPayloadInt::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
