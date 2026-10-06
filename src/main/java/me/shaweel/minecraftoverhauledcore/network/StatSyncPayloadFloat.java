package me.shaweel.minecraftoverhauledcore.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record StatSyncPayloadFloat(String statName, float value) implements CustomPacketPayload {
	public static final Type<StatSyncPayloadFloat> TYPE = new Type<>(Identifier.fromNamespaceAndPath("minecraft_overhauled_core", "stat_sync_long"));
	
	public static final StreamCodec<FriendlyByteBuf, StatSyncPayloadFloat> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, StatSyncPayloadFloat::statName,
		ByteBufCodecs.FLOAT, StatSyncPayloadFloat::value,
		StatSyncPayloadFloat::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
