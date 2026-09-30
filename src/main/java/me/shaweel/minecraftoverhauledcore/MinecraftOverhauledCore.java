package me.shaweel.minecraftoverhauledcore;

import me.shaweel.minecraftoverhauledcore.stats.AttackCooldown;
import me.shaweel.minecraftoverhauledcore.stats.Damage;
import me.shaweel.minecraftoverhauledcore.stats.Health;
import net.fabricmc.api.ModInitializer;

public class MinecraftOverhauledCore implements ModInitializer {
	@Override
	public void onInitialize() {
		Server.initialize();
		Health.initialize();
		Damage.initialize();
		AttackCooldown.initialize();
		FoodHealing.initialize();
	}
}