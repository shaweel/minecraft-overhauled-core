package me.shaweel.minecraftoverhauledcore.mixin.damage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;

@Mixin(Entity.class)
public class FireDamage {
	private static final int fireTicksToGet = 150;
	@Shadow public int remainingFireTicks;
	

	@Inject(method = "setRemainingFireTicks", at = @At("HEAD"), cancellable = true)
	private void setRemainingFireTicks(int remainingTicks, CallbackInfo callbackInfo) {
		if (remainingTicks < remainingFireTicks) return;
		remainingFireTicks = fireTicksToGet;
	}

	@Inject(method = "baseTick", at = @At("HEAD"))
	private void changeFireDamage(CallbackInfo callbackInfo) {
		Entity entity = (Entity)(Object)this;
		boolean inFireBlock = entity.level().getBlockStates(entity.getBoundingBox()).anyMatch(state -> state.is(Blocks.FIRE));

		if (entity.level() instanceof ServerLevel serverLevel && !entity.isInLava() && this.remainingFireTicks > 0 && !inFireBlock) {
			entity.hurtServer(serverLevel, entity.damageSources().onFire(), 1.0F);
		}
	}
}
