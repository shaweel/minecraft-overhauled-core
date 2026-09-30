package me.shaweel.minecraftoverhauledcore;

import java.util.HashMap;
import java.util.UUID;

import me.shaweel.minecraftoverhauledcore.stats.Health;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;

public class FoodHealing {
	private final static int HEAL_TICK = 1;
	private final static int NATURAL_HEAL_TICK = 100;
	private final static int NATURAL_HEAL_PERCENTAGE = 1;
	private final static float PER_HEAL_TICK = 0.1f;

	public static HashMap<UUID,Integer> toHeal = new HashMap<>();
	private static HashMap<UUID,Integer> sinceLastHeal = new HashMap<>();
	private static HashMap<UUID,Integer> sinceLastNaturalHeal = new HashMap<>();

	private static int naturalHeal(int playerSinceLastNaturalHeal, int playerToHeal, Player player) {
		int naturalHealAmount = Math.max(Health.getMaxHealth(player.getUUID()) * NATURAL_HEAL_PERCENTAGE / 100, 1);
		
		if (playerSinceLastNaturalHeal <= 0) {
			toHeal.put(player.getUUID(), playerToHeal + naturalHealAmount);
			sinceLastNaturalHeal.put(player.getUUID(), NATURAL_HEAL_TICK);
		} else {
			sinceLastNaturalHeal.put(player.getUUID(), playerSinceLastNaturalHeal-1);
		}

		return toHeal.get(player.getUUID());
	}

	private static void actuallyHeal(int playerSinceLastHeal, int playerToHeal, Player player) {
		if (playerToHeal <= 0) return;
		
		if (playerSinceLastHeal > 0) {
			sinceLastHeal.put(player.getUUID(), playerSinceLastHeal-1);
			return;
		}

		int healOnce = Math.max(Math.round((playerToHeal * PER_HEAL_TICK)), 1);

		toHeal.put(player.getUUID(), playerToHeal - healOnce);
		Health.heal(player.getUUID(), healOnce);
		sinceLastHeal.put(player.getUUID(), HEAL_TICK);
	}

	private static void heal(Player player) {
		if (!player.isAlive()) return;
		
		int playerSinceLastNaturalHeal = sinceLastNaturalHeal.getOrDefault(player.getUUID(), -1);
		int playerSinceLastHeal = sinceLastHeal.getOrDefault(player.getUUID(), -1);
		int playerToHeal = toHeal.getOrDefault(player.getUUID(), -1);

		if (playerSinceLastNaturalHeal == -1 || playerSinceLastHeal == -1 || playerToHeal == -1) {
			System.err.println("Attempted to heal invalid player");
			return;
		}
		
		playerToHeal = naturalHeal(playerSinceLastNaturalHeal, playerToHeal, player);
		actuallyHeal(playerSinceLastHeal, playerToHeal, player);
	}

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (Player player : server.getPlayerList().getPlayers()) {
				heal(player);
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			Player player = handler.player;

			toHeal.put(player.getUUID(), 0);
			sinceLastHeal.put(player.getUUID(), 0);
			sinceLastNaturalHeal.put(player.getUUID(), NATURAL_HEAL_TICK);
		});
	}
}
