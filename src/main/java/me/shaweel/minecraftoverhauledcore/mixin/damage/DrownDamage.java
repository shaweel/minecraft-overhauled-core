package me.shaweel.minecraftoverhauledcore.mixin.damage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

@Mixin(LivingEntity.class)
public class DrownDamage {
	@Shadow protected boolean shouldTakeDrowningDamage() { return false; }

	@Inject(method = "baseTick", at = @At("HEAD"))
	private void changeDrownDamage(CallbackInfo callbackInfo) {
		LivingEntity livingEntity = (LivingEntity)(Object)this;

		if (livingEntity.isAlive() && livingEntity.level() instanceof ServerLevel level) {
			if (
				livingEntity.isEyeInFluid(FluidTags.WATER) && 
				!level.getBlockState(BlockPos.containing(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ())).is(Blocks.BUBBLE_COLUMN) &&
				!livingEntity.canBreatheUnderwater() &&
				!MobEffectUtil.hasWaterBreathing(livingEntity) &&
				(!(livingEntity instanceof Player) || !((Player)livingEntity).getAbilities().invulnerable) &&
				livingEntity.getAirSupply() <= 0
			) 
			{
				livingEntity.hurtServer(level, livingEntity.damageSources().drown(), 1f);
			}
		}
	}
}
