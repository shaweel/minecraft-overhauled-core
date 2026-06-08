package me.shaweel.minecraftoverhauledcore.mixin;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class RemoveDurability {
	@Inject(method = "hurtAndBreak", at = @At("HEAD"), cancellable = true)
	private void onHurtAndBreak(int amount, ServerLevel level, @Nullable ServerPlayer player, Consumer<Item> onBreak, CallbackInfo callbackinfo) {
		callbackinfo.cancel();
	}
}
