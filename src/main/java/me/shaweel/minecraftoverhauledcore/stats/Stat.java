package me.shaweel.minecraftoverhauledcore.stats;

import java.util.HashMap;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import me.shaweel.minecraftoverhauledcore.network.StatSyncPayloadFloat;
import me.shaweel.minecraftoverhauledcore.network.StatSyncPayloadInt;
import me.shaweel.minecraftoverhauledcore.network.StatSyncPayloadLong;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public class Stat<T> {
	public final String name;
	public final HashMap<UUID, T> statMap = new HashMap<>();
	public final HashMap<EntityType<?>, T> baseMap;
	public final boolean playerOnly;
	public final boolean optional;

	public Stat(String name, HashMap<EntityType<?>, T> baseMap, boolean playerOnly, boolean optional) {
		this.name = name;
		this.baseMap = baseMap;
		this.playerOnly = playerOnly;
		this.optional = optional;

		this.initialize();
	}
	
	private void discardIfInvalid(Entity entity) {
		if (entity instanceof Mob mob && !this.baseMap.containsKey(mob.getType())) {
			mob.discard();
		}
	}

	private void putBaseValue(Entity entity) {
		if (!this.statMap.containsKey(entity.getUUID())) {
			this.statMap.put(entity.getUUID(), this.baseMap.get(entity.getType()));
		}
	}

	public @Nullable T get(Entity entity) {
		UUID uuid = entity.getUUID();
		
		if (!this.statMap.containsKey(uuid)) {
			return null;
		}

		return this.statMap.get(uuid);
	}

	private void sendSyncPacket(ServerPlayer player) {
		T value = this.get(player);

		if (value instanceof Long castedValue) {
			ServerPlayNetworking.send(player, new StatSyncPayloadLong(this.name, castedValue));
		} else if (value instanceof Float castedValue) {
			ServerPlayNetworking.send(player, new StatSyncPayloadFloat(this.name, castedValue));
		} else if (value instanceof Integer castedValue) {
			ServerPlayNetworking.send(player, new StatSyncPayloadInt(this.name, castedValue));
		} else {
			throw new IllegalStateException(String.format("A StatSyncPayload for the type %s does not exist.", value.getClass().getTypeName()));
		}
	}


	private void sendSyncPacketIfPlayer(Entity entity, ServerLevel world) {
		if (entity instanceof ServerPlayer player) {
			world.getServer().execute(() -> sendSyncPacket(player));
		}
	}

	private void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			discardIfInvalid(entity);
			putBaseValue(entity);
			sendSyncPacketIfPlayer(entity, world);
		});
	}
}
