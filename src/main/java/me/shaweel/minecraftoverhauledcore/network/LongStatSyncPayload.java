package me.shaweel.minecraftoverhauledcore.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record LongStatSyncPayload(String name, String entity, long value) implements CustomPacketPayload {
	public static final Type<LongStatSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("minecraft_overhauled_core", "stat_sync_long"));
	
	public static final StreamCodec<FriendlyByteBuf, LongStatSyncPayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, LongStatSyncPayload::name,
		ByteBufCodecs.STRING_UTF8, LongStatSyncPayload::entity,
		ByteBufCodecs.LONG, LongStatSyncPayload::value,
		LongStatSyncPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
