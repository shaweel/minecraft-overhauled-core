package me.shaweel.minecraftoverhauledcore.mixin.damage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shaweel.minecraftoverhauledcore.stats.AttackCooldown;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class AttackIndicator {
	@Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
	private void setAttackStrength(float a, CallbackInfoReturnable<Float> callbackInfoReturnable) {
		Player player = (Player)(Object)this;

		callbackInfoReturnable.setReturnValue(AttackCooldown.getCooldownProgress(player.getUUID()));
		callbackInfoReturnable.cancel();
	}
}
