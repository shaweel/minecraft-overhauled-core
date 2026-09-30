package me.shaweel.minecraftoverhauledcore.mixin.food;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shaweel.minecraftoverhauledcore.FoodHealing;
import me.shaweel.minecraftoverhauledcore.stats.Health;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class EatWhenLow {
	@Shadow private Abilities abilities;

	@Inject(method = "canEat", at = @At("HEAD"), cancellable = true)
	private void eatWhenLow(boolean canAlwaysEat, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		Player player = (Player)(Object)this;

		float futureHealth = Health.getCurrentHealth(player.getUUID()) + FoodHealing.toHeal.get(player.getUUID());
		boolean low = futureHealth < Health.getMaxHealth(player.getUUID());

		callbackInfoReturnable.setReturnValue(abilities.invulnerable || canAlwaysEat || low);
	}
}
