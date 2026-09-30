package me.shaweel.minecraftoverhauledcore.stats;

import java.util.HashMap;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
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

	public void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			discardIfInvalid(entity);
			putBaseValue(entity);
			sendPacketIfPlayer(entity, world);
		});
	}
}
