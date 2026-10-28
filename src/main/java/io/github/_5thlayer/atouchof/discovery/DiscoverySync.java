// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.discovery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * Discovery's sync: the server tells each player's client what is known of each Pillar's things, in a
 * {@link KnownPayload}, whenever it differs from what that client was last told, and never anyone else.
 * The Runtime's own wiring: a Pillar needs only {@link Discovery}.
 */
public final class DiscoverySync {

    /** What each logged-in player's client was last told of each Pillar's things, kept from their join to their logout. */
    private static final Map<UUID, Map<String, Set<String>>> TOLD = new HashMap<>();
    private static final List<BiConsumer<ServerPlayer, KnownPayload>> SEND_LISTENERS = new CopyOnWriteArrayList<>();

    private DiscoverySync() {
    }

    public static void register(IEventBus modBus) {
        // Optional, so a client whose highest nested Runtime predates it still connects, and knows nothing (ADR-0002).
        modBus.addListener(RegisterPayloadHandlersEvent.class, event -> event.registrar("1").optional()
                .playToClient(KnownPayload.TYPE, KnownPayload.STREAM_CODEC, (payload, context) -> ClientDiscovery.told(payload)));
        modBus.addListener(FMLCommonSetupEvent.class, event -> Discovery.close());
        // Posted for a joining player as they log in, and for everyone on a datapack reload.
        NeoForge.EVENT_BUS.addListener(OnDatapackSyncEvent.class, event -> event.getRelevantPlayers()
                .forEach(player -> Discovery.all().forEach(discovery -> tell(player, discovery))));
        NeoForge.EVENT_BUS.addListener(AdvancementEvent.AdvancementProgressEvent.class, DiscoverySync::progressed);
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, event -> TOLD.remove(event.getEntity().getUUID()));
    }

    /** Adds a listener that hears of each payload sent to a player's client from now on, whether or not that client can receive it. */
    public static void onSend(BiConsumer<ServerPlayer, KnownPayload> listener) {
        SEND_LISTENERS.add(listener);
    }

    /** Tells the player's client what is known of the Pillar's things, if that differs from what it was last told. */
    static void tell(ServerPlayer player, Discovery discovery) {
        var known = discovery.known(player);
        var told = TOLD.computeIfAbsent(player.getUUID(), id -> new HashMap<>());
        if (known.equals(told.put(discovery.namespace(), known))) {
            return;
        }
        var payload = new KnownPayload(discovery.namespace(), known);
        SEND_LISTENERS.forEach(listener -> listener.accept(player, payload));
        // A connection that never negotiated the channel, as a fake player's, can't be sent it.
        if (player.connection != null && player.connection.hasChannel(KnownPayload.TYPE)) {
            player.connection.send(payload);
        }
    }

    private static void progressed(AdvancementEvent.AdvancementProgressEvent event) {
        var discovery = Discovery.owning(event.getAdvancement().id());
        if (discovery != null && event.getEntity() instanceof ServerPlayer player) {
            tell(player, discovery);
        }
    }
}
