package com.truedarkness.client;

import com.truedarkness.TrueDarknessMod;
import com.truedarkness.config.DarknessConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * Перехватывает расчёт lightmap и обнуляет минимальный уровень яркости.
 * Это то же самое что делает Hardcore Darkness / True Darkness —
 * убирает "ambient" минимум который ванилла добавляет всегда.
 */
@EventBusSubscriber(modid = TrueDarknessMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class DarknessClientHandler {

    // Последнее рассчитанное значение яркости для lightmap
    private static float cachedNightBrightness = 0.0f;
    private static boolean serverEnforced = false;

    public static void setServerEnforced(boolean enforced) {
        serverEnforced = enforced;
    }

    public static boolean isActive() {
        // Активен если сервер принудил, или просто стоит мод (клиент)
        return true;
    }

    /**
     * Возвращает минимальную яркость для блочного освещения.
     * Ванилла использует значение ~0.05, мы возвращаем 0.
     */
    public static float getMinBlockBrightness() {
        if (!isActive()) return -1f; // -1 = не вмешиваемся
        return (float)(double) DarknessConfig.CLIENT.caveDarkness.get();
    }

    /**
     * Возвращает минимальную яркость для небесного освещения (ночь).
     * С учётом фазы луны если включено в конфиге.
     */
    public static float getMinSkyBrightness() {
        if (!isActive()) return -1f;

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;

        if (level == null) return -1f;

        double base = DarknessConfig.CLIENT.nightDarkness.get();

        if (DarknessConfig.CLIENT.moonPhases.get()) {
            // Фаза луны: 0 = новолуние (0.0), 4 = полнолуние (~0.25)
            int moonPhase = level.getMoonPhase();
            // moonPhase идёт 0..7, полнолуние = 0 в ванилле
            // Добавляем чуть яркости на полнолуние
            float moonBonus = 0.0f;
            switch (moonPhase) {
                case 0 -> moonBonus = 0.20f;  // полнолуние
                case 1, 7 -> moonBonus = 0.10f;
                case 2, 6 -> moonBonus = 0.05f;
                default -> moonBonus = 0.0f;   // новолуние и другие — полная тьма
            }
            return (float) base + moonBonus;
        }

        return (float) base;
    }
}
