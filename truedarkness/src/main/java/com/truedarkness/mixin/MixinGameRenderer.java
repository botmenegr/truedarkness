package com.truedarkness.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Заглушка — оставлено для будущего расширения.
 * Например можно добавить эффект виньетки в темноте.
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    // Пусто — расширяем при необходимости
}
