package me.shaweel.minecraftoverhauledcore.mixin.damage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

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
	private static final int inFireTickDuration = 3;
	private static final int inFirePercentagePerInFireTick = 2;
	private static final int onFireTickDuration = 5;
	private static final int onFirePercentagePerOnFireTick = 2;
	private static final int inLavaTickDuration = 1;
	private static final int inLavaPercentagePerInLavaTick = 3;
	private static final int onDrownTickDuration = 3;
	private static final int onDrownPercentagePerOnDrownTick = 2;

	private static class TickMaps {
		private static final HashMap<UUID, Long> inFireTickMap = new HashMap<>();
		private static final HashMap<UUID, Long> onFireTickMap = new HashMap<>();
		private static final HashMap<UUID, Long> inLavaTickMap = new HashMap<>();
		private static final HashMap<UUID, Long> onDrownTickMap = new HashMap<>();
	}

	private ServerLevel lastLevel;

	@Shadow
	protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }

	@Shadow
	protected void playHurtSound(DamageSource source) {}

	@Shadow
	private void playSecondaryHurtSound(DamageSource source) {}

	@SuppressWarnings("unchecked")
	private void clearAllTickMaps() {
		for (Field field : TickMaps.class.getDeclaredFields()) {
			field.setAccessible(true);
			try {
				HashMap<UUID, Long> hashMap = (HashMap<UUID, Long>)(field.get(null));
				hashMap.clear();
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void tryTick(LivingEntity hitEntity, ServerLevel level, DamageSource source, int tickDuration, int percentagePerTick, HashMap<UUID, Long> tickMap) {		
		if (lastLevel == null) {
			clearAllTickMaps();
			lastLevel = level;
		}
		else if (!lastLevel.equals(level)) {
			clearAllTickMaps();
			lastLevel = level;
		}

		UUID uuid = hitEntity.getUUID();
		long currentTick = level.getGameTime();
		long nextTick = tickMap.getOrDefault(uuid, 0l);

		if (currentTick < nextTick) return;

		tickMap.put(uuid, currentTick + tickDuration);
		tick(hitEntity, level, source, percentagePerTick);
	}

	private void tick(LivingEntity hitEntity, ServerLevel level, DamageSource source, int percentagePerTick) {
		float idealDamage = Health.getMaxHealth(hitEntity.getUUID()) / 100 * percentagePerTick;
		int finalDamage = Math.max(1, Math.round(idealDamage));
		actuallyHurt(hitEntity, level, source, finalDamage);
	}

	private void dealKnockback(LivingEntity hitEntity, DamageSource source) {
		System.out.println(hitEntity.getName());
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
		
		hitEntity.knockback(0.4, xd, zd, source, 67); //The last argument(float damage) literally isn't even used in the function
		hitEntity.indicateDamage(xd, zd);
	}

	private void playHurtSounds(LivingEntity hitEntity, DamageSource source) {
		if (hitEntity.isDeadOrDying()) {
			hitEntity.makeSound(getDeathSound());
		} else {
			playHurtSound(source);
		}
		playSecondaryHurtSound(source);
	}

	private void actuallyHurt(LivingEntity hitEntity, ServerLevel level, DamageSource source, float damage) {
		level.broadcastDamageEvent(hitEntity, source);
		if (source.getEntity() != null) {
			hitEntity.hurtMarked = true;
			dealKnockback(hitEntity, source);
		}

		playHurtSounds(hitEntity, source);

		if (source.getEntity() == null) {
			Health.hurt(hitEntity.getUUID(), (int)damage);
		} else {
			Health.hurt(hitEntity.getUUID(), source.getEntity().getUUID());
		}
	}

	@Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
	private void hurtServer(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		LivingEntity hitEntity = (LivingEntity)(Object)this;

		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

		if ((hitEntity.isDeadOrDying()) ||
		(source.is(DamageTypeTags.IS_FIRE) && hitEntity.hasEffect(MobEffects.FIRE_RESISTANCE))) {
			callbackInfoReturnable.setReturnValue(false);
			return;
		} else if (source.is(DamageTypes.LAVA)) {
			tryTick(hitEntity, level, source, inLavaTickDuration, inLavaPercentagePerInLavaTick, TickMaps.inLavaTickMap);
			callbackInfoReturnable.setReturnValue(false);
			return;
		} else if (source.is(DamageTypes.IN_FIRE)) {
			tryTick(hitEntity, level, source, inFireTickDuration, inFirePercentagePerInFireTick, TickMaps.inFireTickMap);
			callbackInfoReturnable.setReturnValue(false);
			return;
		} else if (source.is(DamageTypes.ON_FIRE)) {
			tryTick(hitEntity, level, source, onFireTickDuration, onFirePercentagePerOnFireTick, TickMaps.onFireTickMap);
			callbackInfoReturnable.setReturnValue(false);
			return;
		} else if (source.is(DamageTypes.DROWN)) {
			tryTick(hitEntity, level, source, onDrownTickDuration, onDrownPercentagePerOnDrownTick, TickMaps.onDrownTickMap);
			callbackInfoReturnable.setReturnValue(false);
			return;
		} else if (source.is(DamageTypeTags.IS_PLAYER_ATTACK) && AttackCooldown.getCooldownProgress(source.getEntity().getUUID()) < 1.0f) {
			AttackCooldown.markAttack(source.getEntity().getUUID());
			callbackInfoReturnable.setReturnValue(false);
			return;
		}

		actuallyHurt(hitEntity, level, source, damage);

		callbackInfoReturnable.setReturnValue(false);
	}
}
