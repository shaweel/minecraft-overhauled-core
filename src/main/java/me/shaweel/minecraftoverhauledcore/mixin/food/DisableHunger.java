package me.shaweel.minecraftoverhauledcore.mixin.food;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.food.FoodData;

@Mixin(FoodData.class)
public class DisableHunger {
	@Inject(method = "setFoodLevel", at = @At("HEAD"), cancellable = true)
	private void disableHungerDepleeting(int food, CallbackInfo callbackInfo) {
		callbackInfo.cancel();
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void disableHungerHealing(CallbackInfo callbackInfo) {
		callbackInfo.cancel();
	}
}
