// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.meter;

import java.util.Objects;

import net.minecraft.resources.Identifier;

/**
 * How a meter's icons look in one of its states: three GUI sprites of the Pillar's own, 9 by 9 like
 * vanilla's food icons, under {@code assets/<namespace>/textures/gui/sprites/}. The empty sprite is
 * drawn under the full and the half ones, so those may leave it showing through.
 */
public record MeterState(Identifier full, Identifier half, Identifier empty) {

    public MeterState {
        Objects.requireNonNull(full, "full");
        Objects.requireNonNull(half, "half");
        Objects.requireNonNull(empty, "empty");
    }
}
