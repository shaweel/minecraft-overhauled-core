package me.shaweel.minecraftoverhauledcore.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import me.shaweel.minecraftoverhauledcore.stats.AttackCooldown;
import me.shaweel.minecraftoverhauledcore.stats.Health;

import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class HealthHurt {
	@Shadow
	protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }

	@Shadow
	protected void playHurtSound(final DamageSource source) {}

	@Shadow
	private void playSecondaryHurtSound(final DamageSource source) {}

	@Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
	private void hurtServer(final ServerLevel level, final DamageSource source, float damage, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		LivingEntity hitEntity = (LivingEntity)(Object)this;

		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

		if ((hitEntity.isDeadOrDying()) ||
		(source.is(DamageTypeTags.IS_FIRE) && hitEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) || 
		(source.is(DamageTypeTags.IS_PLAYER_ATTACK) && AttackCooldown.getCooldownProgress(source.getEntity().getUUID()) == 1.0f)) {
			callbackInfoReturnable.setReturnValue(false);
			return;
		}
		
		hitEntity.hurtMarked = true;
		level.broadcastDamageEvent(hitEntity, source);

		if (!source.is(DamageTypeTags.NO_KNOCKBACK)) {
			double xd = 0.0;
			double zd = 0.0;
			if (source.getDirectEntity() instanceof Projectile projectile) {
				DoubleDoubleImmutablePair knockbackDirection = projectile.calculateHorizontalHurtKnockbackDirection(hitEntity, source);
				xd = -knockbackDirection.leftDouble();
				zd = -knockbackDirection.rightDouble();
			} else if (source.getSourcePosition() != null) {
				xd = source.getSourcePosition().x() - hitEntity.getX();
				zd = source.getSourcePosition().z() - hitEntity.getZ();
			}
			
			hitEntity.knockback(0.4F, xd, zd);
			hitEntity.indicateDamage(xd, zd);
		}

		if (hitEntity.isDeadOrDying()) {
			hitEntity.makeSound(getDeathSound());
		} else {
			playHurtSound(source);
		}
		playSecondaryHurtSound(source);

		if (source.getEntity() == null) {
			Health.hurt(hitEntity.getUUID(), (int)damage);
		} else {
			Health.hurt(hitEntity.getUUID(), source.getEntity().getUUID());
		}

		callbackInfoReturnable.setReturnValue(false);
	}
}
