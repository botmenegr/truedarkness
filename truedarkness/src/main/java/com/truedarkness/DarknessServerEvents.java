package com.truedarkness;

import com.truedarkness.network.DarknessNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Серверные события.
 * Когда игрок заходит на сервер — отправляем ему пакет "включи темноту".
 */
@EventBusSubscriber(modid = TrueDarknessMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DarknessServerEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player,
                    new DarknessNetwork.DarknessEnforcePayload(true));
            TrueDarknessMod.LOGGER.info("Sent darkness enforcement to player: {}",
                    player.getName().getString());
        }
    }
}
