// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * What each player's client was last told of each key, so it is told only what is news to it.
 * The Runtime's own wiring, for its syncs: a Pillar never needs it.
 */
public final class Told<K, V> {

    private final Map<UUID, Map<K, V>> told = new HashMap<>();

    /** Records the value as told to the player, and answers whether it differs from what they were last told of the key. */
    public boolean tell(UUID player, K key, V value) {
        return !value.equals(told.computeIfAbsent(player, id -> new HashMap<>()).put(key, value));
    }

    /** Forgets the key for the player, and answers whether they had been told of it. */
    public boolean clear(UUID player, K key) {
        var keys = told.get(player);
        return keys != null && keys.remove(key) != null;
    }

    /** Forgets everything the player was told, as their client forgets it when they log out. */
    public void forget(UUID player) {
        told.remove(player);
    }
}
