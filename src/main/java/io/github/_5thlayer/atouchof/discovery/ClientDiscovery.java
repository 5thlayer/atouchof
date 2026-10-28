// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.discovery;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * What this client was last told its player has discovered, by Pillar namespace. It never reads the
 * advancements themselves, knows nothing until it is told, and forgets it all as its player leaves a world.
 */
final class ClientDiscovery {

    private static final Map<String, Set<String>> KNOWN = new ConcurrentHashMap<>();

    private ClientDiscovery() {
    }

    static boolean isKnown(String namespace, String id) {
        return KNOWN.getOrDefault(namespace, Set.of()).contains(id);
    }

    static void told(KnownPayload payload) {
        KNOWN.put(payload.namespace(), payload.known());
    }

    static void forget() {
        KNOWN.clear();
    }
}
