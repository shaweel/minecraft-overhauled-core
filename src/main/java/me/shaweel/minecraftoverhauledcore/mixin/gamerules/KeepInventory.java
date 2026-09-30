package me.shaweel.minecraftoverhauledcore.mixin.gamerules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRules;

@Mixin(GameRules.class)
public class KeepInventory {
	@Shadow
	private static GameRule<Boolean> registerBoolean(String id, GameRuleCategory category, boolean defaultValue) { return null; }

	@Inject(method = "registerBoolean", at = @At("HEAD"), cancellable = true)
	private static void forceKeepInventory(String id, GameRuleCategory category, boolean defaultValue, CallbackInfoReturnable<GameRule<Boolean>> callbackInfoReturnable) {
		if (!id.equals("keep_inventory") || defaultValue == true) return;

		callbackInfoReturnable.setReturnValue(registerBoolean(id, category, true));
		callbackInfoReturnable.cancel();
	}
}
