package me.shaweel.minecraftoverhauledcore.mixin.damage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shaweel.minecraftoverhauledcore.Utils;
import me.shaweel.minecraftoverhauledcore.stats.Health;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

@Mixin(LivingEntity.class)
public class FallDamage {
	@Inject(method = "calculateFallDamage", at = @At("HEAD"), cancellable = true)
	private void calculateFallDamage(final double fallDistance, final float damageModifier, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
		LivingEntity entity = (LivingEntity)(Object)this;

		if (entity.is(EntityTypeTags.FALL_DAMAGE_IMMUNE) || Utils.getEntity(entity.getUUID()) == null) {
			callbackInfoReturnable.setReturnValue(0);
			callbackInfoReturnable.cancel();
			return;
		}

		double multiplier = Math.max(0.1 * Health.getMaxHealth(entity.getUUID()), 1);
		int realFallDistance = Mth.floor(Math.round(fallDistance) - entity.getAttributeValue(Attributes.SAFE_FALL_DISTANCE));
		int total = Mth.floor(multiplier) * realFallDistance;
		callbackInfoReturnable.setReturnValue((int)(total));
		callbackInfoReturnable.cancel();
	}
}
