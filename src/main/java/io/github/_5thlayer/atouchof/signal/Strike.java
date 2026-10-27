// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.signal;

import java.util.Objects;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

/**
 * The Strike signal: this player was struck by the Dark. It says what happened and nothing about what
 * it costs, which belongs to each listener (ADR-0004). One Pillar raises it and any Pillar may listen,
 * neither knowing the other is there.
 *
 * <p>It is posted on NeoForge's game bus, {@link NeoForge#EVENT_BUS}, so a Pillar listens the usual
 * way, with {@code @SubscribeEvent} or {@code NeoForge.EVENT_BUS.addListener((Strike strike) -> ...)}.
 * It cannot be cancelled.
 */
public final class Strike extends Event {

    private final ServerPlayer player;

    private Strike(ServerPlayer player) {
        this.player = player;
    }

    /**
     * Raises the Strike for the player, on the server thread: each listener hears it once, now. The
     * Runtime passes it on whatever the difficulty, the game mode or the damage the Strike dealt, and
     * with no listener it does nothing.
     */
    public static void raise(ServerPlayer player) {
        NeoForge.EVENT_BUS.post(new Strike(Objects.requireNonNull(player, "player")));
    }

    /** The player who was struck. */
    public ServerPlayer player() {
        return player;
    }
}
