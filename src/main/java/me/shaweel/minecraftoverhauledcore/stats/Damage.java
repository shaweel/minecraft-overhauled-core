package me.shaweel.minecraftoverhauledcore.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.EntityType;

public class Damage {
	public static final HashMap<EntityType<?>, Integer> baseDamage = new HashMap<>(Map.ofEntries(
		Map.entry(EntityType.PLAYER, 3),
		Map.entry(EntityType.ZOMBIE, 30),
		Map.entry(EntityType.SKELETON, 30),
		Map.entry(EntityType.CREEPER, 0),
		Map.entry(EntityType.SPIDER, 30),
		Map.entry(EntityType.PIGLIN, 100),
		Map.entry(EntityType.CAVE_SPIDER, 50),
		Map.entry(EntityType.POLAR_BEAR, 0),
		Map.entry(EntityType.COW, 0),
		Map.entry(EntityType.PIG, 0),
		Map.entry(EntityType.RABBIT, 0),
		Map.entry(EntityType.SHEEP, 0),
		Map.entry(EntityType.CHICKEN, 0)
	));

	public static final HashMap<UUID, Integer> damageMap = new HashMap<>();

	public static void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (!baseDamage.containsKey(entity.getType())) return;
			
			if (!damageMap.containsKey(entity.getUUID())) {
				damageMap.put(entity.getUUID(), baseDamage.get(entity.getType()));
			}
		});
	}
}
