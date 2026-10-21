package me.shaweel.minecraftoverhauledcore;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class CurrentServer {
	private static MinecraftServer currentServer = null;

	public static void initialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			currentServer = server;
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			currentServer = null;
		});
	}

	public static @Nullable MinecraftServer get() {
		return currentServer;
	}
}
