// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.sync;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ToldTest {

    private final Told<String, Integer> told = new Told<>();
    private final UUID player = UUID.randomUUID();
    private final UUID other = UUID.randomUUID();

    @Test
    void theFirstValueIsNews() {
        assertTrue(told.tell(player, "meter", 10));
    }

    @Test
    void theSameValueAgainIsNot() {
        told.tell(player, "meter", 10);
        assertFalse(told.tell(player, "meter", 10));
    }

    @Test
    void aChangedValueIsNews() {
        told.tell(player, "meter", 10);
        assertTrue(told.tell(player, "meter", 9));
    }

    @Test
    void eachKeyIsToldApart() {
        told.tell(player, "meter", 10);
        assertTrue(told.tell(player, "another", 10));
    }

    @Test
    void eachPlayerIsToldApart() {
        told.tell(player, "meter", 10);
        assertTrue(told.tell(other, "meter", 10));
    }

    @Test
    void aClearOfWhatWasToldIsNews() {
        told.tell(player, "meter", 10);
        assertTrue(told.clear(player, "meter"));
    }

    @Test
    void aClearOfWhatWasNeverToldIsNot() {
        assertFalse(told.clear(player, "meter"));
        told.tell(other, "meter", 10);
        assertFalse(told.clear(player, "meter"));
    }

    @Test
    void aSecondClearIsNot() {
        told.tell(player, "meter", 10);
        told.clear(player, "meter");
        assertFalse(told.clear(player, "meter"));
    }

    @Test
    void theSameValueAfterAClearIsNews() {
        told.tell(player, "meter", 10);
        told.clear(player, "meter");
        assertTrue(told.tell(player, "meter", 10));
    }

    @Test
    void theSameValueAfterTheyAreForgottenIsNews() {
        told.tell(player, "meter", 10);
        told.forget(player);
        assertTrue(told.tell(player, "meter", 10));
    }

    @Test
    void forgettingOnePlayerKeepsTheOthers() {
        told.tell(player, "meter", 10);
        told.tell(other, "meter", 10);
        told.forget(player);
        assertFalse(told.tell(other, "meter", 10));
    }
}
