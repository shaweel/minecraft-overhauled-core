package me.shaweel.minecraftoverhauledcore.client.mixin;

import java.util.stream.IntStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shaweel.minecraftoverhauledcore.client.stats.ClientHealth;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

@Mixin(Gui.class)
public class HealthBar {
	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void onRenderHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
		int health = ClientHealth.health;
		int currentHealth = ClientHealth.currentHealth;
		if (health <= 0) return;
		float healthPercentage = (float) currentHealth / health;

		int screenWidth = graphics.guiWidth();
		int screenHeight = graphics.guiHeight();

		final int barAndTexturePadding = 2;
		final int heartTextureResolution = 9;

		int barWidth = 165;
		int barHeight = 5;
		int textureX = screenWidth / 2 - barWidth / 2 - heartTextureResolution / 2 - barAndTexturePadding / 2 - 2;
		int barX = textureX + barAndTexturePadding + heartTextureResolution;
		int barY = screenHeight - 32;
		int textureY = barY + (barHeight + 2) + Math.ceilDiv(heartTextureResolution - (barHeight + 2), 2) - heartTextureResolution;

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath("minecraft", "hud/heart/container"), textureX, textureY, 9, 9);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath("minecraft", "hud/heart/full"), textureX, textureY, 9, 9);

		IntStream.rangeClosed(1, barHeight).forEachOrdered(index -> {
			int offset;
			if (barHeight % 2 == 0) {
				int minCenter = barHeight / 2;
				int maxCenter = barHeight / 2 + 1;
				if (index > barHeight / 2) {
					offset = Math.abs(index - maxCenter);
				} else {
					offset = Math.abs(index - minCenter);
				}
			} else {
				int center = Math.ceilDiv(barHeight, 2);
				offset = Math.abs(index - center);
			}
			graphics.fill(barX + offset, barY + index, barX + barWidth - offset, barY + index + 1, 0xFF292929);
			int maxHealthX = barX + (int)(barWidth * healthPercentage); 
			if (maxHealthX > barX + barWidth - offset) maxHealthX = barX + barWidth - offset;
			if (currentHealth != 0) {
				graphics.fill(barX + offset, barY + index, maxHealthX, barY + index + 1, 0xFFF81C1E);
			}

			if (index == 1) {
				graphics.fill(barX + offset, barY + index - 1, barX + barWidth - offset, barY + index, 0xFF000000);
			} else if (index == barHeight) {
				graphics.fill(barX + offset, barY + index + 1, barX + barWidth - offset, barY + index + 2, 0xFF000000);
			} else if (barHeight % 2 != 0 && index == Math.ceilDiv(barHeight, 2)) {
				graphics.fill(barX + offset, barY + index - 1, barX + offset + 1, barY + index, 0xFF000000);
				graphics.fill(barX + offset - 1, barY + index, barX + offset, barY + index + 1, 0xFF000000);
				graphics.fill(barX + offset, barY + index + 1, barX + offset + 1, barY + index + 2, 0xFF000000);
				graphics.fill(barX + barWidth - offset - 1, barY + index - 1, barX + barWidth - offset, barY + index, 0xFF000000);
				graphics.fill(barX + barWidth - offset, barY + index, barX + barWidth - offset + 1, barY + index + 1, 0xFF000000);
				graphics.fill(barX + barWidth - offset - 1, barY + index + 1, barX + barWidth - offset, barY + index + 2, 0xFF000000);
			} else if (barHeight % 2 == 0 && index == barHeight / 2) {
				graphics.fill(barX + offset, barY + index - 1, barX + offset + 1, barY + index, 0xFF000000);
				graphics.fill(barX + offset - 1, barY + index, barX + offset, barY + index + 1, 0xFF000000);
				graphics.fill(barX + barWidth - offset - 1, barY + index - 1, barX + barWidth - offset, barY + index, 0xFF000000);
				graphics.fill(barX + barWidth - offset, barY + index, barX + barWidth - offset + 1, barY + index + 1, 0xFF000000);
			} else if (barHeight % 2 == 0 && index == barHeight / 2 + 1) {
				graphics.fill(barX + offset, barY + index + 1, barX + offset + 1, barY + index + 2, 0xFF000000);
				graphics.fill(barX + offset - 1, barY + index, barX + offset, barY + index + 1, 0xFF000000);
				graphics.fill(barX + barWidth - offset - 1, barY + index + 1, barX + barWidth - offset, barY + index + 2, 0xFF000000);
				graphics.fill(barX + barWidth - offset, barY + index, barX + barWidth - offset + 1, barY + index + 1, 0xFF000000);
			} else if (index < (float)barHeight / 2) {
				graphics.fill(barX + offset, barY + index - 1, barX + offset + 1, barY + index, 0xFF000000);
				graphics.fill(barX + barWidth - offset - 1, barY + index - 1, barX + barWidth - offset, barY + index, 0xFF000000);
			} else if (index > (float)barHeight / 2) {
				graphics.fill(barX + offset, barY + index + 1, barX + offset + 1, barY + index + 2, 0xFF000000);
				graphics.fill(barX + barWidth - offset - 1, barY + index + 1, barX + barWidth - offset, barY + index + 2, 0xFF000000);
			}
		});

		Font font = Minecraft.getInstance().font;

		String text = String.format("%s/%s", currentHealth, health);
		graphics.text(font, text, barX + barWidth / 2 - font.width(text) / 2, barY - font.lineHeight / 2, 0xFFFFFFFF);
	}
}
