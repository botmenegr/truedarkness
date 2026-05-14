package com.truedarkness.mixin;

import com.truedarkness.client.DarknessClientHandler;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Главный миксин — перехватывает updateLightTexture и обнуляет
 * минимальный уровень яркости (ambient light).
 *
 * Ванилла в LightTexture#updateLightTexture считает так:
 *   float f = dimension.ambientLight()  // обычно 0.0 для Overworld
 *   ... но потом добавляет минимум через gamma
 *
 * Мы заменяем значение гаммы на 0 чтобы убрать "засветку".
 */
@Mixin(LightTexture.class)
public class MixinLightTexture {

    /**
     * Перехватываем переменную которая хранит значение гаммы игрока
     * (опция Brightness в настройках).
     * Заменяем на 0.0 чтобы гамма не добавляла минимальную яркость.
     */
    @ModifyVariable(
        method = "updateLightTexture",
        at = @At(
            value = "INVOKE",
            // Точка: после того как посчитан blockBrightness + skyBrightness
            target = "Lnet/minecraft/util/Mth;clamp(FFF)F",
            ordinal = 0
        ),
        ordinal = 0
    )
    private float modifyGamma(float original) {
        float minBrightness = DarknessClientHandler.getMinBlockBrightness();
        if (minBrightness < 0) return original; // мод не активен
        // Возвращаем 0 вместо гаммы игрока — убирает минимальную яркость
        return 0.0f;
    }

    /**
     * Дополнительно: убираем ambient skylight ночью.
     * Перехватываем skyLight brightness перед финальным расчётом.
     */
    @ModifyVariable(
        method = "updateLightTexture",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;clamp(FFF)F",
            ordinal = 1
        ),
        ordinal = 0
    )
    private float modifySkyBrightness(float original) {
        float minSky = DarknessClientHandler.getMinSkyBrightness();
        if (minSky < 0) return original;
        // Разрешаем максимум из нашего минимума и рассчитанного значения
        return Math.max(original, minSky);
    }
}
