package me.shaweel.minecraftoverhauledcore.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record StatSyncPayloadLong(String statName, long value) implements CustomPacketPayload {
	public static final Type<StatSyncPayloadLong> TYPE = new Type<>(Identifier.fromNamespaceAndPath("minecraft_overhauled_core", "stat_sync_long"));
	
	public static final StreamCodec<FriendlyByteBuf, StatSyncPayloadLong> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, StatSyncPayloadLong::statName,
		ByteBufCodecs.LONG, StatSyncPayloadLong::value,
		StatSyncPayloadLong::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
