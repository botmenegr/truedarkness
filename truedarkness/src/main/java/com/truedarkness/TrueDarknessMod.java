package com.truedarkness;

import com.truedarkness.config.DarknessConfig;
import com.truedarkness.network.DarknessNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TrueDarknessMod.MOD_ID)
public class TrueDarknessMod {

    public static final String MOD_ID = "truedarkness";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public TrueDarknessMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, DarknessConfig.CLIENT_SPEC);

        modEventBus.addListener(this::commonSetup);

        DarknessNetwork.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("True Darkness initialized. Lights out.");
    }
}
