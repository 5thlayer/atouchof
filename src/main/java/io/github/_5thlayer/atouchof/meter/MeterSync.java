// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.meter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

import io.github._5thlayer.atouchof.sync.Told;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * The meters' sync: the server tells each player's client what each meter shows them, in a
 * {@link MeterPayload}, whenever it changes from what that client was last told, and never anyone else;
 * and tells it, in a {@link MeterClearPayload}, to stop drawing a meter it was told of.
 * The Runtime's own wiring: a Pillar needs only {@link Meter}.
 */
public final class MeterSync {

    /** What each logged-in player's client was last told of each meter, kept from their join to their logout. */
    private static final Told<Identifier, MeterPayload> TOLD = new Told<>();
    private static final List<BiConsumer<ServerPlayer, MeterPayload>> SEND_LISTENERS = new CopyOnWriteArrayList<>();
    private static final List<BiConsumer<ServerPlayer, Identifier>> CLEAR_LISTENERS = new CopyOnWriteArrayList<>();

    private MeterSync() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
            var registrar = event.registrar("1");
            registrar.playToClient(MeterPayload.TYPE, MeterPayload.STREAM_CODEC, (payload, context) -> ClientMeters.told(payload));
            // Optional, so a client whose highest nested Runtime predates it still connects, and keeps the row (ADR-0002).
            registrar.optional().playToClient(MeterClearPayload.TYPE, MeterClearPayload.STREAM_CODEC,
                    (payload, context) -> ClientMeters.cleared(payload.meter()));
        });
        modBus.addListener(FMLCommonSetupEvent.class, event -> Meter.close());
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, event -> TOLD.forget(event.getEntity().getUUID()));
    }

    /** Adds a listener that hears of each payload sent to a player's client from now on, whether or not that client can receive it. */
    public static void onSend(BiConsumer<ServerPlayer, MeterPayload> listener) {
        SEND_LISTENERS.add(listener);
    }

    /** Adds a listener that hears of each meter cleared from a player's client from now on, whether or not that client can receive it. */
    public static void onClear(BiConsumer<ServerPlayer, Identifier> listener) {
        CLEAR_LISTENERS.add(listener);
    }

    static void tell(ServerPlayer player, MeterPayload payload) {
        if (!TOLD.tell(player.getUUID(), payload.meter(), payload)) {
            return;
        }
        SEND_LISTENERS.forEach(listener -> listener.accept(player, payload));
        send(player, payload);
    }

    static void clear(ServerPlayer player, Identifier meter) {
        if (!TOLD.clear(player.getUUID(), meter)) {
            return;
        }
        CLEAR_LISTENERS.forEach(listener -> listener.accept(player, meter));
        send(player, new MeterClearPayload(meter));
    }

    private static void send(ServerPlayer player, CustomPacketPayload payload) {
        // A connection that never negotiated the channel, as a fake player's, can't be sent it.
        if (player.connection != null && player.connection.hasChannel(payload.type())) {
            player.connection.send(payload);
        }
    }
}
