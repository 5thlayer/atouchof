// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.meter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.Identifier;

/**
 * What this client was last told of each meter, for {@link MeterHud} to draw. A meter it has not been
 * told of, or was told to clear, is not drawn, and it forgets them all as its player leaves a world.
 */
final class ClientMeters {

    private static final Map<Identifier, MeterPayload> TOLD = new ConcurrentHashMap<>();

    private ClientMeters() {
    }

    static void told(MeterPayload payload) {
        TOLD.put(payload.meter(), payload);
    }

    static void cleared(Identifier meter) {
        TOLD.remove(meter);
    }

    /** What the client was told of this meter, or null. */
    static MeterPayload of(Identifier meter) {
        return TOLD.get(meter);
    }

    static void forget() {
        TOLD.clear();
    }
}
