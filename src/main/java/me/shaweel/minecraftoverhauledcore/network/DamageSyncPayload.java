package me.shaweel.minecraftoverhauledcore.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record DamageSyncPayload(int damage) implements CustomPacketPayload {
	public static final Type<DamageSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("minecraft_overhauled_core", "damage_sync"));
	
	public static final StreamCodec<FriendlyByteBuf, DamageSyncPayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, DamageSyncPayload::damage,
		DamageSyncPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
