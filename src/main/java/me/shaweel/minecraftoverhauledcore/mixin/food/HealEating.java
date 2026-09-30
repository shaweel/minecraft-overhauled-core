package me.shaweel.minecraftoverhauledcore.mixin.food;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shaweel.minecraftoverhauledcore.FoodHealing;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;

@Mixin(FoodProperties.class)
public class HealEating {
	@Inject(method = "onConsume", at = @At("HEAD"), cancellable = true)
	public void healEating(Level level, LivingEntity user, ItemStack stack, Consumable consumable, CallbackInfo callbackInfo) {
		if (level.isClientSide()) return;
		FoodProperties food = (FoodProperties)(Object)this;

		RandomSource random = user.getRandom();
		level.playSound(null, user.getX(), user.getY(), user.getZ(), consumable.sound().value(), SoundSource.NEUTRAL, 1.0F, random.triangle(1.0F, 0.4F));
		if (user instanceof Player player) {
			int toHeal = FoodHealing.toHeal.get(player.getUUID());
			FoodHealing.toHeal.put(player.getUUID(), toHeal + food.nutrition());
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, Mth.randomBetween(random, 0.9F, 1.0F));
		}
		callbackInfo.cancel();
	}
}
