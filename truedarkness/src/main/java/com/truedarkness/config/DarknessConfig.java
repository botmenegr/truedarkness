package com.truedarkness.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class DarknessConfig {

    public static final ModConfigSpec CLIENT_SPEC;
    public static final DarknessConfig CLIENT;

    // Минимальная яркость ночью (0.0 = полная тьма)
    public final ModConfigSpec.DoubleValue nightDarkness;
    // Минимальная яркость в пещерах (0.0 = полная тьма)
    public final ModConfigSpec.DoubleValue caveDarkness;
    // Включить зависимость от фазы луны
    public final ModConfigSpec.BooleanValue moonPhases;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("True Darkness - настройки темноты").push("darkness");

        ModConfigSpec.Builder clientBuilder = new ModConfigSpec.Builder();
        CLIENT = new DarknessConfig(clientBuilder);
        CLIENT_SPEC = clientBuilder.build();
    }

    public DarknessConfig(ModConfigSpec.Builder builder) {
        builder.comment("True Darkness - настройки").push("darkness");

        nightDarkness = builder
                .comment("Яркость ночью. 0.0 = полная тьма, 1.0 = без изменений")
                .defineInRange("nightDarkness", 0.0, 0.0, 1.0);

        caveDarkness = builder
                .comment("Яркость в пещерах. 0.0 = полная тьма, 1.0 = без изменений")
                .defineInRange("caveDarkness", 0.0, 0.0, 1.0);

        moonPhases = builder
                .comment("Учитывать фазы луны (полнолуние чуть светлее)")
                .define("moonPhases", true);

        builder.pop();
    }
}
