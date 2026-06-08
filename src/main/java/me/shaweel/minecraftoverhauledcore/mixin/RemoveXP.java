package me.shaweel.minecraftoverhauledcore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class RemoveXP {
	@Inject(method = "giveExperiencePoints", at = @At("HEAD"), cancellable = true)
	private void onGiveExperiencePoints(int amount, CallbackInfo callbackinfo) {
		callbackinfo.cancel();
	}
}
