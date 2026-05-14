package com.truedarkness.network;

import com.truedarkness.TrueDarknessMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Сетевой слой.
 * Сервер при подключении игрока отправляет пакет DarknessEnforcePayload.
 * Клиент получает его и активирует темноту.
 *
 * Также: мод регистрируется как REQUIRED на обеих сторонах —
 * это означает что без мода на клиенте подключиться к серверу невозможно.
 */
public class DarknessNetwork {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(DarknessNetwork::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(TrueDarknessMod.MOD_ID)
                .versioned("1.0.0")
                // REQUIRED = без мода на клиенте — не пускает на сервер
                .optional(); // меняй на .versioned() если хочешь обязательный

        registrar.playToClient(
                DarknessEnforcePayload.TYPE,
                DarknessEnforcePayload.STREAM_CODEC,
                DarknessEnforcePayload::handle
        );
    }

    /**
     * Пакет от сервера к клиенту: "активируй темноту".
     */
    public record DarknessEnforcePayload(boolean enforce) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<DarknessEnforcePayload> TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TrueDarknessMod.MOD_ID, "enforce"));

        public static final StreamCodec<FriendlyByteBuf, DarknessEnforcePayload> STREAM_CODEC =
                StreamCodec.of(
                        (buf, payload) -> buf.writeBoolean(payload.enforce()),
                        buf -> new DarknessEnforcePayload(buf.readBoolean())
                );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(DarknessEnforcePayload payload,
                net.neoforged.neoforge.network.handling.IPayloadContext context) {
            context.enqueueWork(() -> {
                com.truedarkness.client.DarknessClientHandler.setServerEnforced(payload.enforce());
                TrueDarknessMod.LOGGER.info("True Darkness: server enforcement = {}", payload.enforce());
            });
        }
    }
}
