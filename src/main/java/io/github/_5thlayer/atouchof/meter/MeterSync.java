// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.meter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * The meters' sync: the server tells each player's client what each meter shows them, in a
 * {@link MeterPayload}, whenever it changes from what that client was last told, and never anyone else.
 */
public final class MeterSync {

    /** What each logged-in player's client was last told of each meter, kept from their join to their logout. */
    private static final Map<UUID, Map<Identifier, MeterPayload>> TOLD = new HashMap<>();
    private static final List<BiConsumer<ServerPlayer, MeterPayload>> SEND_LISTENERS = new CopyOnWriteArrayList<>();

    private MeterSync() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(RegisterPayloadHandlersEvent.class, event -> event.registrar("1")
                .playToClient(MeterPayload.TYPE, MeterPayload.STREAM_CODEC, (payload, context) -> ClientMeters.told(payload)));
        modBus.addListener(FMLCommonSetupEvent.class, event -> Meter.close());
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, event -> TOLD.remove(event.getEntity().getUUID()));
    }

    /** Adds a listener that hears of each payload sent to a player's client from now on, whether or not that client can receive it. */
    public static void onSend(BiConsumer<ServerPlayer, MeterPayload> listener) {
        SEND_LISTENERS.add(listener);
    }

    static void tell(ServerPlayer player, MeterPayload payload) {
        var told = TOLD.computeIfAbsent(player.getUUID(), id -> new HashMap<>());
        if (payload.equals(told.put(payload.meter(), payload))) {
            return;
        }
        SEND_LISTENERS.forEach(listener -> listener.accept(player, payload));
        // A connection that never negotiated the channel, as a fake player's, can't be sent it.
        if (player.connection != null && player.connection.hasChannel(MeterPayload.TYPE)) {
            player.connection.send(payload);
        }
    }
}
