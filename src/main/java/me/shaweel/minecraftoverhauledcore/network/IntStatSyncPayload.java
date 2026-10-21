package me.shaweel.minecraftoverhauledcore.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record IntStatSyncPayload(String name, String player, int value) implements CustomPacketPayload {
	public static final Type<IntStatSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("minecraft_overhauled_core", "stat_sync_int"));
	
	public static final StreamCodec<FriendlyByteBuf, IntStatSyncPayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, IntStatSyncPayload::name,
		ByteBufCodecs.STRING_UTF8, IntStatSyncPayload::player,
		ByteBufCodecs.INT, IntStatSyncPayload::value,
		IntStatSyncPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
