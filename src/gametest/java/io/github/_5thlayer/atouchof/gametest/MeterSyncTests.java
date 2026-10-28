// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.gametest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.authlib.GameProfile;
import io.github._5thlayer.atouchof.meter.Meter;
import io.github._5thlayer.atouchof.meter.MeterPayload;
import io.github._5thlayer.atouchof.meter.MeterState;
import io.github._5thlayer.atouchof.meter.MeterSync;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * A meter a Pillar sets reaches its player's client, and only theirs, and only when what it draws
 * changes (#9); one it clears is taken off that client only if it was shown (#11).
 */
final class MeterSyncTests {

    private static final MeterState STATE = new MeterState(
            Identifier.withDefaultNamespace("hud/food_full"),
            Identifier.withDefaultNamespace("hud/food_half"),
            Identifier.withDefaultNamespace("hud/food_empty"));

    /** A meter of the tests' own, with a maximum of 100 and two states. */
    private static Meter meter;

    /** The payloads sent to each player, by their id, since the test that joined them began. */
    private static final Map<UUID, List<MeterPayload>> SENT = new ConcurrentHashMap<>();
    /** How many times the tests' meter was cleared from each player's client, by their id. */
    private static final Map<UUID, Integer> CLEARED = new ConcurrentHashMap<>();

    private MeterSyncTests() {
    }

    /** Registers the tests' meter while the mods are constructed, as a Pillar does, and hears each payload sent. */
    static void listen() {
        meter = Meter.register(ATouchOfGameTests.id("test"), 100, STATE, STATE);
        MeterSync.onSend((player, payload) -> {
            if (payload.meter().equals(meter.id())) {
                SENT.computeIfAbsent(player.getUUID(), id -> new ArrayList<>()).add(payload);
            }
        });
        MeterSync.onClear((player, cleared) -> {
            if (cleared.equals(meter.id())) {
                CLEARED.merge(player.getUUID(), 1, Integer::sum);
            }
        });
    }

    static void register(ATouchOfGameTests.Registrar tests) {
        tests.test("meter_sends_to_its_player_only", 20, MeterSyncTests::sendsToItsPlayerOnly);
        tests.test("meter_sends_again_only_when_its_row_changes", 20, MeterSyncTests::sendsAgainOnlyWhenItsRowChanges);
        tests.test("meter_sends_again_after_a_relog", 20, MeterSyncTests::sendsAgainAfterARelog);
        tests.test("meter_clears_from_its_player_only", 20, MeterSyncTests::clearsFromItsPlayerOnly);
        tests.test("meter_clears_only_when_shown", 20, MeterSyncTests::clearsOnlyWhenShown);
        tests.test("meter_sends_again_when_set_after_a_clear", 20, MeterSyncTests::sendsAgainWhenSetAfterAClear);
        tests.test("meter_refuses_a_state_or_trend_out_of_range", 20, MeterSyncTests::refusesAStateOrTrendOutOfRange);
        tests.test("meter_refuses_a_registration_once_the_mods_are_set_up", 20, MeterSyncTests::refusesALateRegistration);
    }

    private static void sendsToItsPlayerOnly(GameTestHelper helper) {
        var shown = join(helper);
        var other = join(helper);
        meter.set(shown, 50, 1, -2);
        expectSent(helper, shown, List.of(new MeterPayload(meter.id(), 10, 1, -2)));
        expectSent(helper, other, List.of());
        helper.succeed();
    }

    private static void sendsAgainOnlyWhenItsRowChanges(GameTestHelper helper) {
        var player = join(helper);
        meter.set(player, 50, 0, 0);
        meter.set(player, 50, 0, 0);
        // 49.5 fills the same ten half steps as 50.
        meter.set(player, 49.5f, 0, 0);
        meter.set(player, 44, 0, 0);
        meter.set(player, 44, 1, 0);
        meter.set(player, 44, 1, 3);
        expectSent(helper, player, List.of(
                new MeterPayload(meter.id(), 10, 0, 0),
                new MeterPayload(meter.id(), 9, 0, 0),
                new MeterPayload(meter.id(), 9, 1, 0),
                new MeterPayload(meter.id(), 9, 1, 3)));
        helper.succeed();
    }

    private static void sendsAgainAfterARelog(GameTestHelper helper) {
        var profile = TestPlayers.profile();
        meter.set(join(helper, profile), 100, 0, 1);
        TestPlayers.leave(helper, profile);
        meter.set(join(helper, profile), 100, 0, 1);
        expectSent(helper, TestPlayers.playerList(helper).getPlayer(profile.id()), List.of(
                new MeterPayload(meter.id(), 20, 0, 1),
                new MeterPayload(meter.id(), 20, 0, 1)));
        helper.succeed();
    }

    private static void clearsFromItsPlayerOnly(GameTestHelper helper) {
        var cleared = join(helper);
        var other = join(helper);
        meter.set(cleared, 50, 0, 0);
        meter.set(other, 50, 0, 0);
        meter.clear(cleared);
        expectCleared(helper, cleared, 1);
        expectCleared(helper, other, 0);
        helper.succeed();
    }

    private static void clearsOnlyWhenShown(GameTestHelper helper) {
        var player = join(helper);
        meter.clear(player);
        expectCleared(helper, player, 0);
        meter.set(player, 50, 0, 0);
        meter.clear(player);
        meter.clear(player);
        expectCleared(helper, player, 1);
        helper.succeed();
    }

    private static void sendsAgainWhenSetAfterAClear(GameTestHelper helper) {
        var player = join(helper);
        meter.set(player, 50, 0, 0);
        meter.clear(player);
        meter.set(player, 50, 0, 0);
        expectSent(helper, player, List.of(
                new MeterPayload(meter.id(), 10, 0, 0),
                new MeterPayload(meter.id(), 10, 0, 0)));
        helper.succeed();
    }

    private static void refusesAStateOrTrendOutOfRange(GameTestHelper helper) {
        var player = join(helper);
        expectRefused(helper, () -> meter.set(player, 50, 2, 0), "a state past the meter's two");
        expectRefused(helper, () -> meter.set(player, 50, -1, 0), "a negative state");
        expectRefused(helper, () -> meter.set(player, 50, 0, 4), "a trend band above +3");
        expectRefused(helper, () -> meter.set(player, 50, 0, -4), "a trend band below -3");
        expectSent(helper, player, List.of());
        helper.succeed();
    }

    private static void refusesALateRegistration(GameTestHelper helper) {
        try {
            Meter.register(ATouchOfGameTests.id("late"), 100, STATE);
        } catch (IllegalStateException expected) {
            helper.succeed();
            return;
        }
        helper.fail("a meter registered once the mods were set up");
    }

    private static ServerPlayer join(GameTestHelper helper) {
        return join(helper, TestPlayers.profile());
    }

    private static ServerPlayer join(GameTestHelper helper, GameProfile profile) {
        var player = TestPlayers.join(helper, profile);
        TestPlayers.whenDone(helper, () -> {
            TestPlayers.leave(helper, profile);
            SENT.remove(profile.id());
            CLEARED.remove(profile.id());
        });
        return player;
    }

    private static void expectSent(GameTestHelper helper, ServerPlayer player, List<MeterPayload> expected) {
        var sent = SENT.getOrDefault(player.getUUID(), List.of());
        if (!sent.equals(expected)) {
            helper.fail("sent " + sent + ", expected " + expected);
        }
    }

    private static void expectCleared(GameTestHelper helper, ServerPlayer player, int expected) {
        var cleared = CLEARED.getOrDefault(player.getUUID(), 0);
        if (cleared != expected) {
            helper.fail("cleared " + cleared + " times, expected " + expected);
        }
    }

    private static void expectRefused(GameTestHelper helper, Runnable set, String what) {
        try {
            set.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        helper.fail("accepted " + what);
    }
}
