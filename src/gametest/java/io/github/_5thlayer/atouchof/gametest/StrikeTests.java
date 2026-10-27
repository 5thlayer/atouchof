// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.gametest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github._5thlayer.atouchof.signal.Strike;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.neoforged.neoforge.common.NeoForge;

/** A raised Strike reaches each listener once, naming its player, on every difficulty, and is harmless unheard (#10). */
final class StrikeTests {

    private StrikeTests() {
    }

    static void register(ATouchOfGameTests.Registrar tests) {
        tests.test("strike_reaches_a_listener_on_peaceful", 20, helper -> reachesAListener(helper, Difficulty.PEACEFUL));
        tests.test("strike_reaches_a_listener_on_hard", 20, helper -> reachesAListener(helper, Difficulty.HARD));
        tests.test("strike_with_no_listener_is_harmless", 20, StrikeTests::harmlessUnheard);
    }

    private static void reachesAListener(GameTestHelper helper, Difficulty difficulty) {
        var server = helper.getLevel().getServer();
        var before = server.getWorldData().getDifficulty();
        server.setDifficulty(difficulty, true);
        var player = join(helper);
        var other = join(helper);
        // Only this test's players: other tests on the shared server raise Strikes of their own.
        var heard = new ArrayList<ServerPlayer>();
        Consumer<Strike> listener = strike -> {
            if (strike.player() == player || strike.player() == other) {
                heard.add(strike.player());
            }
        };
        NeoForge.EVENT_BUS.addListener(Strike.class, listener);
        try {
            Strike.raise(player);
        } finally {
            NeoForge.EVENT_BUS.unregister(listener);
            server.setDifficulty(before, true);
        }
        if (!heard.equals(List.of(player))) {
            helper.fail("heard Strikes for " + heard + " on " + difficulty + ", expected one for " + player);
        }
        helper.succeed();
    }

    private static void harmlessUnheard(GameTestHelper helper) {
        Strike.raise(join(helper));
        helper.succeed();
    }

    private static ServerPlayer join(GameTestHelper helper) {
        var profile = TestPlayers.profile();
        var player = TestPlayers.join(helper, profile);
        TestPlayers.whenDone(helper, () -> TestPlayers.leave(helper, profile));
        return player;
    }
}
