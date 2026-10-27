// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.meter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import io.github._5thlayer.atouchof.meter.MeterRow.Icon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MeterRowTest {

    @ParameterizedTest
    @CsvSource({
            "0, 100, 0",
            "100, 100, 20",
            "50, 100, 10",
            // A value between two half steps shows the higher, as vanilla's hearts do.
            "0.1, 100, 1",
            "99.9, 100, 20",
            "51, 100, 11",
            "7, 20, 7",
            "1, 3, 7",
            // Out of range is drawn at the end of the row.
            "-5, 100, 0",
            "150, 100, 20",
    })
    void halvesAreTheValueInTwentiethsOfTheMaximum(float value, float max, int halves) {
        assertEquals(halves, MeterRow.halves(value, max));
    }

    @Test
    void aFullRowIsTenFullIcons() {
        assertEquals(List.of(Icon.FULL, Icon.FULL, Icon.FULL, Icon.FULL, Icon.FULL,
                Icon.FULL, Icon.FULL, Icon.FULL, Icon.FULL, Icon.FULL), row(20));
    }

    @Test
    void anEmptyRowIsTenEmptyIcons() {
        assertEquals(List.of(Icon.EMPTY, Icon.EMPTY, Icon.EMPTY, Icon.EMPTY, Icon.EMPTY,
                Icon.EMPTY, Icon.EMPTY, Icon.EMPTY, Icon.EMPTY, Icon.EMPTY), row(0));
    }

    @Test
    void anOddCountEndsInAHalfIcon() {
        // Place 0 is the first drawn, the row's right end, as vanilla's food bar fills.
        assertEquals(List.of(Icon.FULL, Icon.FULL, Icon.FULL, Icon.HALF, Icon.EMPTY,
                Icon.EMPTY, Icon.EMPTY, Icon.EMPTY, Icon.EMPTY, Icon.EMPTY), row(7));
    }

    @Test
    void oneHalfIsAHalfIconAtTheRightEnd() {
        assertEquals(Icon.HALF, MeterRow.icon(1, 0));
        assertEquals(Icon.EMPTY, MeterRow.icon(1, 1));
    }

    @Test
    void nineteenHalvesEndInAHalfIconAtTheLeftEnd() {
        assertEquals(Icon.FULL, MeterRow.icon(19, 8));
        assertEquals(Icon.HALF, MeterRow.icon(19, 9));
    }

    @ParameterizedTest
    @CsvSource({"-3, 3", "-2, 2", "-1, 1", "0, 0", "1, 1", "2, 2", "3, 3"})
    void aTrendBandDrawsAsManyChevronsAsItsSize(int trend, int chevrons) {
        assertEquals(chevrons, MeterRow.chevrons(trend));
    }

    @ParameterizedTest
    @CsvSource({"-3, false", "-1, false", "1, true", "3, true"})
    void aPositiveBandPointsUpAndANegativeOneDown(int trend, boolean up) {
        assertEquals(up, MeterRow.pointsUp(trend));
    }

    @ParameterizedTest
    @CsvSource({"-9, 3", "4, 3"})
    void aBandOutOfRangeDrawsNoMoreThanThreeChevrons(int trend, int chevrons) {
        // A newer server's band, which this client can't place.
        assertEquals(chevrons, MeterRow.chevrons(trend));
    }

    private static List<Icon> row(int halves) {
        var icons = new ArrayList<Icon>();
        for (var place = 0; place < MeterRow.ICONS; place++) {
            icons.add(MeterRow.icon(halves, place));
        }
        return icons;
    }
}
