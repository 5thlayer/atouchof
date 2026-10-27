// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof;

import io.github._5thlayer.atouchof.meter.MeterSync;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * The Runtime's entry point. A Pillar nests this jar, and FML loads the newest nested copy once. It
 * syncs and draws the Pillars' meters (ADR-0005), and carries their signals, such as the
 * {@link io.github._5thlayer.atouchof.signal.Strike}, on the game bus (ADR-0004). The game tests hook themselves in from the
 * {@code gametest} source set, which never ships (libworks ADR 0011).
 */
@Mod(ATouchOf.MOD_ID)
public final class ATouchOf {

    /** The mod id, which gradle.properties' {@code mod_id} must match. */
    public static final String MOD_ID = "atouchof";

    public ATouchOf(IEventBus modBus) {
        MeterSync.register(modBus);
    }
}
