package me.shaweel.minecraftoverhauledcore.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

@Mixin(Gui.class)
public class RemoveUnusedHUDElements {
	@Inject(method = "extractHearts", at = @At("HEAD"), cancellable = true)
	private void onExtractHearts(
		GuiGraphicsExtractor graphics,
		Player player,
		int xLeft,
		int yLineBase,
		int healthRowHeight,
		int heartOffsetIndex,
		float maxHealth,
		int currentHealth,
		int oldHealth,
		int absorption,
		boolean blink,
		CallbackInfo callbackInfo
	) {
		callbackInfo.cancel();
	}

	@Inject(method = "extractArmor", at = @At("HEAD"), cancellable = true)
	private static void onExtractArmor(
		GuiGraphicsExtractor graphics, Player player, int yLineBase, int numHealthRows, 
		int healthRowHeight, int xLeft, CallbackInfo callbackInfo
	) {
		callbackInfo.cancel();
	}

	@Inject(method = "extractFood", at = @At("HEAD"), cancellable = true)
	private void onExtractFood(GuiGraphicsExtractor graphics, Player player, int yLineBase, int xRight, CallbackInfo callbackInfo) {
		callbackInfo.cancel();
	}

	@Inject(method = "nextContextualInfoState", at = @At("HEAD"), cancellable = true)
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void onNextContextualInfoState(CallbackInfoReturnable<Object> callbackInfoReturnable) {
		try {
			Object empty = Enum.valueOf((Class<Enum>) Class.forName("net.minecraft.client.gui.Gui$ContextualInfo"), "EMPTY");
			callbackInfoReturnable.setReturnValue(empty);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
