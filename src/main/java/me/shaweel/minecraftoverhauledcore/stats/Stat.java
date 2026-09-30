package me.shaweel.minecraftoverhauledcore.stats;

import java.util.HashMap;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.EntityType;

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
	}
	
	public static void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			discardIfInvalid(entity);
			putBaseHealthValues(entity);
			sendHealthSyncPacketIfPlayer(entity, world);
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, _) -> {
			UUID uuid = newPlayer.getUUID();

			setCurrentHealth(uuid, getMaxHealth(uuid));
		});
	}
}
