package me.shaweel.minecraftoverhauledcore;

import java.util.UUID;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class Utils {
	public static Entity getEntity(UUID uuid) {
		for (ServerLevel level : Server.server.getAllLevels()) {
			Entity entity = level.getEntity(uuid);
			if (entity != null) return entity;
		}
		return null;
	}

	public static void killEntity(UUID uuid) {
		for (ServerLevel level : Server.server.getAllLevels()) {
			Entity entity = level.getEntity(uuid);
			if (entity != null) entity.kill(level);
		}
	}
}
