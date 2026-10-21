package me.shaweel.minecraftoverhauledcore.stats;

import java.util.HashMap;
import java.util.UUID;

import me.shaweel.minecraftoverhauledcore.CurrentServer;
import me.shaweel.minecraftoverhauledcore.network.IntStatSyncPayload;
import me.shaweel.minecraftoverhauledcore.network.LongStatSyncPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public class Stat<T> {
	public final String name;
	public String description;
	public Identifier icon;
	public final HashMap<UUID, T> statMap = new HashMap<>();
	public final HashMap<EntityType<?>, T> baseMap;
	public final boolean playerOnly;
	public final boolean visible;

	public Stat(String name, HashMap<EntityType<?>, T> baseMap, boolean playerOnly) {
		this.name = name;
		this.baseMap = baseMap;
		this.playerOnly = playerOnly;
		this.visible = false;

		this.initialize();
	}
	
	public Stat(String name, String description, Identifier icon, HashMap<EntityType<?>, T> baseMap, boolean playerOnly) {
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.baseMap = baseMap;
		this.playerOnly = playerOnly;
		this.visible = true;

		this.initialize();
	}
	
	private void discardIfInvalid(Entity entity) {
		if (entity instanceof Mob mob && !this.playerOnly && !this.baseMap.containsKey(mob.getType())) {
			mob.discard();
		}
	}

	private void putBaseValue(Entity entity) {
		if (!this.statMap.containsKey(entity.getUUID())) {
			this.statMap.put(entity.getUUID(), this.baseMap.get(entity.getType()));
		}
	}

	private void sendSyncPacket(ServerPlayer player, String entity, T value) {
		Class<?> type = value.getClass();

		if (type == Long.class)
			ServerPlayNetworking.send(player, new LongStatSyncPayload(this.name, entity, (long) value));
		else if (type == Integer.class) {
			ServerPlayNetworking.send(player, new IntStatSyncPayload(this.name, entity, (int) value));
		} else {
			throw new IllegalStateException("There is no Payload for this data type");
		}
	}

	private void sendSyncPacketIfPlayer(Entity entity) {
		UUID UUID = entity.getUUID();
		String UUIDString = UUID.toString();

		for (ServerPlayer player : CurrentServer.get().getPlayerList().getPlayers()) {
			this.sendSyncPacket(player, UUIDString, this.statMap.get(UUID));
		}
	}

	public void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			discardIfInvalid(entity);
			putBaseValue(entity);
			sendSyncPacketIfPlayer(entity);
		});
	}
}
