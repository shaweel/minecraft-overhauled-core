package me.shaweel.minecraftoverhauledcore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class RemoveHunger {
	@Inject(method = "causeFoodExhaustion", at = @At("HEAD"), cancellable = true)
	private void onCauseFoodExhaustion(float amount, CallbackInfo callbackinfo) {
		callbackinfo.cancel();
	}
}
