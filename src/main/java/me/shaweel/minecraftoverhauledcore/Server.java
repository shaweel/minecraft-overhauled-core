package me.shaweel.minecraftoverhauledcore;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class Server {
	public static MinecraftServer server;

	public static void initialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);
        	ServerLifecycleEvents.SERVER_STOPPED.register(s -> server = null);
	}
}
