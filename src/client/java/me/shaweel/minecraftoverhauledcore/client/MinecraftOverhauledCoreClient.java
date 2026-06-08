package me.shaweel.minecraftoverhauledcore.client;

import me.shaweel.minecraftoverhauledcore.client.stats.ClientHealth;
import net.fabricmc.api.ClientModInitializer;

public class MinecraftOverhauledCoreClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientHealth.initialize();
	}
}