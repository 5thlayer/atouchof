// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.meter;

/**
 * How a meter's row reads: ten icons in half steps, as vanilla's food bar, and chevrons for its trend.
 * Place 0 is the row's right end, where vanilla's food bar fills from.
 */
final class MeterRow {

    /** The icons in a row. */
    static final int ICONS = 10;

    /** The half steps in a full row. */
    static final int HALVES = ICONS * 2;

    enum Icon { FULL, HALF, EMPTY }

    private MeterRow() {
    }

    /**
     * The half steps a value fills, out of {@link #HALVES}. A value between two steps shows the higher,
     * as vanilla rounds a mount's health up into its hearts, so only an empty meter shows empty.
     */
    static int halves(float value, float max) {
        var halves = (int) Math.ceil(value * HALVES / max);
        return Math.clamp(halves, 0, HALVES);
    }

    /** The icon at a place in a row that fills this many half steps. */
    static Icon icon(int halves, int place) {
        if (place * 2 + 1 < halves) {
            return Icon.FULL;
        }
        return place * 2 + 1 == halves ? Icon.HALF : Icon.EMPTY;
    }

    /** The chevrons a trend band draws, at most {@value Meter#TREND_LIMIT}. */
    static int chevrons(int trend) {
        return Math.min(Math.abs(trend), Meter.TREND_LIMIT);
    }

    /** Whether a trend band's chevrons point up, as a positive band's do, or down. */
    static boolean pointsUp(int trend) {
        return trend > 0;
    }
}
