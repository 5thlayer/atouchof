// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.meter;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * A Pillar's meter, which the Runtime syncs to each player's client and draws as a row of ten icons in
 * half steps, with chevrons for its trend (ADR-0005). The stat behind it stays the Pillar's: the
 * Runtime only receives what the meter shows, and computes nothing from it (ADR-0002).
 *
 * <p>A Pillar registers its meter while the mods are constructed, on both sides, so each client knows
 * its sprites, and sets it each tick for each player, on the server thread.
 */
public final class Meter {

    /** The lowest and highest trend band. */
    public static final int TREND_LIMIT = 3;

    /** Every meter, by id, in the order the client stacks them: by namespace, then path, where Identifier's own order takes the path first. */
    private static final Map<Identifier, Meter> METERS = new ConcurrentSkipListMap<>(Comparator.comparing(Identifier::toString));

    private static volatile boolean closed;

    private final Identifier id;
    private final float max;
    private final List<MeterState> states;

    private Meter(Identifier id, float max, List<MeterState> states) {
        this.id = id;
        this.max = max;
        this.states = states;
    }

    /**
     * Registers a meter, from a Pillar's mod constructor. Several Pillars' meters stack above vanilla's
     * food bar in the order of their ids, as {@code namespace:path} strings.
     *
     * @param id     the meter's id, in the Pillar's namespace
     * @param max    the value that fills the row
     * @param states the meter's states, each with its own sprites; a Pillar sets one by its index
     * @throws IllegalStateException    once the mods are set up, or for an id already registered
     * @throws IllegalArgumentException for a maximum that is not positive, or no states
     */
    public static Meter register(Identifier id, float max, MeterState... states) {
        Objects.requireNonNull(id, "id");
        if (closed) {
            throw new IllegalStateException("meter " + id + " registered once the mods were set up; register it from the mod's constructor");
        }
        if (!(max > 0)) {
            throw new IllegalArgumentException("meter " + id + " has a maximum of " + max + ", which must be positive");
        }
        if (states.length == 0) {
            throw new IllegalArgumentException("meter " + id + " has no states");
        }
        var meter = new Meter(id, max, List.of(states));
        if (METERS.putIfAbsent(id, meter) != null) {
            throw new IllegalStateException("meter " + id + " is already registered");
        }
        return meter;
    }

    /**
     * Sets what the meter shows the player: its value, the index of its state, and its trend band from
     * −{@value #TREND_LIMIT} to +{@value #TREND_LIMIT}, which draws that many chevrons, up for a positive
     * band and down for a negative one. The Runtime tells the player's client only when what it draws
     * changes, so a Pillar may set it every tick.
     *
     * @throws IllegalArgumentException for a state or a trend band out of range
     */
    public void set(ServerPlayer player, float value, int state, int trend) {
        if (state < 0 || state >= states.size()) {
            throw new IllegalArgumentException("meter " + id + " has " + states.size() + " states, and no state " + state);
        }
        if (Math.abs(trend) > TREND_LIMIT) {
            throw new IllegalArgumentException("meter " + id + " was set a trend band of " + trend + ", outside ±" + TREND_LIMIT);
        }
        MeterSync.tell(player, new MeterPayload(id, MeterRow.halves(value, max), state, trend));
    }

    public Identifier id() {
        return id;
    }

    public List<MeterState> states() {
        return states;
    }

    /** Every meter, in the order the client stacks them. */
    static Collection<Meter> all() {
        return METERS.values();
    }

    /** Refuses any registration from now on, as the mods are set up. */
    static void close() {
        closed = true;
    }
}
