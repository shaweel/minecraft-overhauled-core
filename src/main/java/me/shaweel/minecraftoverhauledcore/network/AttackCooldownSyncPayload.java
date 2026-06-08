package me.shaweel.minecraftoverhauledcore.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record AttackCooldownSyncPayload(int attackCooldown, float attackCooldownProgress) implements CustomPacketPayload {
	public static final Type<AttackCooldownSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("minecraft_overhauled_core", "health_sync"));
	
	public static final StreamCodec<FriendlyByteBuf, AttackCooldownSyncPayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, AttackCooldownSyncPayload::attackCooldown,
		ByteBufCodecs.FLOAT, AttackCooldownSyncPayload::attackCooldownProgress,
		AttackCooldownSyncPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
