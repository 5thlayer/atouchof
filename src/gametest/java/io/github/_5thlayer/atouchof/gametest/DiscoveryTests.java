// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.gametest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.authlib.GameProfile;
import io.github._5thlayer.atouchof.discovery.Discovery;
import io.github._5thlayer.atouchof.discovery.DiscoverySync;
import io.github._5thlayer.atouchof.discovery.KnownPayload;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;

/**
 * A Pillar's Discovery record (ADR-0006, #12), on two records of the tests' own. {@code atouchof} has
 * two things: {@code seen}, whose advancement only a test grants, and {@code removed}, which has no
 * advancement and so counts as known. {@code atouchof_switch} has one, {@code seen}, and is switched
 * off but while a test switches it on.
 */
final class DiscoveryTests {

    private static IEventBus modBus;
    private static Discovery record;
    private static Discovery switched;
    private static volatile boolean switchedOn;

    /** The payloads sent to each player, by their id, since the test that joined them began. */
    private static final Map<UUID, List<KnownPayload>> SENT = new ConcurrentHashMap<>();

    private DiscoveryTests() {
    }

    /** Registers the tests' records while the mods are constructed, as a Pillar does, and hears each payload sent. */
    static void listen(IEventBus bus) {
        modBus = bus;
        record = Discovery.register(modBus, "atouchof", List.of("seen", "removed"), () -> true);
        switched = Discovery.register(modBus, "atouchof_switch", List.of("seen"), () -> switchedOn);
        DiscoverySync.onSend((player, payload) -> SENT.computeIfAbsent(player.getUUID(), id -> new ArrayList<>()).add(payload));
    }

    static void register(ATouchOfGameTests.Registrar tests) {
        tests.test("discovery_a_thing_is_known_once_its_advancement_is_done_or_if_it_has_none", 20, DiscoveryTests::knownOnceDone);
        tests.test("discovery_tells_the_client_on_login_and_on_each_change_only", 20, DiscoveryTests::tellsOnEachChange);
        tests.test("discovery_tells_only_its_player", 20, DiscoveryTests::tellsOnlyItsPlayer);
        tests.test("discovery_tells_again_after_a_relog", 20, DiscoveryTests::tellsAgainAfterARelog);
        tests.test("discovery_switched_off_tells_everything_known", 20, DiscoveryTests::switchedOff);
        tests.test("discovery_tells_again_on_a_datapack_reload", 20, DiscoveryTests::tellsAgainOnADatapackReload);
        tests.test("discovery_refuses_a_thing_it_does_not_have", 20, DiscoveryTests::refusesAnUnknownThing);
        tests.test("discovery_refuses_a_registration_once_the_mods_are_set_up", 20, DiscoveryTests::refusesALateRegistration);
    }

    private static void knownOnceDone(GameTestHelper helper) {
        var player = join(helper);
        if (record.isKnown(player, "seen")) {
            helper.fail("seen is known before its advancement is done");
        }
        if (!record.isKnown(player, "removed")) {
            helper.fail("removed, with no advancement, is not known");
        }
        award(helper, player, "seen");
        if (!record.isKnown(player, "seen")) {
            helper.fail("seen is not known once its advancement is done");
        }
        helper.succeed();
    }

    private static void tellsOnEachChange(GameTestHelper helper) {
        var player = join(helper);
        award(helper, player, "seen");
        // The root is one of the record's advancements, but no thing, so nothing known changes.
        var root = advancement(helper, record.root());
        player.getAdvancements().award(root, "joined");
        player.getAdvancements().revoke(advancement(helper, record.advancementId("seen")), "seen");
        expectSent(helper, player, record, List.of(Set.of("removed"), Set.of("removed", "seen"), Set.of("removed")));
        helper.succeed();
    }

    private static void tellsOnlyItsPlayer(GameTestHelper helper) {
        var discoverer = join(helper);
        var other = join(helper);
        award(helper, discoverer, "seen");
        expectSent(helper, discoverer, record, List.of(Set.of("removed"), Set.of("removed", "seen")));
        expectSent(helper, other, record, List.of(Set.of("removed")));
        helper.succeed();
    }

    private static void tellsAgainAfterARelog(GameTestHelper helper) {
        var profile = TestPlayers.profile();
        join(helper, profile);
        TestPlayers.leave(helper, profile);
        var player = join(helper, profile);
        expectSent(helper, player, record, List.of(Set.of("removed"), Set.of("removed")));
        helper.succeed();
    }

    private static void switchedOff(GameTestHelper helper) {
        var player = join(helper);
        if (!switched.isKnown(player, "seen")) {
            helper.fail("seen is not known with the record switched off");
        }
        switchedOn = true;
        try {
            switched.retell();
        } finally {
            switchedOn = false;
        }
        switched.retell();
        expectSent(helper, player, switched, List.of(Set.of("seen"), Set.of(), Set.of("seen")));
        helper.succeed();
    }

    private static void tellsAgainOnADatapackReload(GameTestHelper helper) {
        var player = join(helper);
        var players = helper.getLevel().getServer().getPlayerList();
        switchedOn = true;
        try {
            players.reloadResources();
        } finally {
            switchedOn = false;
        }
        players.reloadResources();
        expectSent(helper, player, switched, List.of(Set.of("seen"), Set.of(), Set.of("seen")));
        helper.succeed();
    }

    private static void refusesAnUnknownThing(GameTestHelper helper) {
        var player = join(helper);
        try {
            record.isKnown(player, "unheard_of");
        } catch (IllegalArgumentException expected) {
            helper.succeed();
            return;
        }
        helper.fail("answered for a thing the record does not have");
    }

    private static void refusesALateRegistration(GameTestHelper helper) {
        try {
            Discovery.register(modBus, "atouchof_late", List.of(), () -> true);
        } catch (IllegalStateException expected) {
            helper.succeed();
            return;
        }
        helper.fail("a record registered once the mods were set up");
    }

    private static ServerPlayer join(GameTestHelper helper) {
        return join(helper, TestPlayers.profile());
    }

    private static ServerPlayer join(GameTestHelper helper, GameProfile profile) {
        var player = TestPlayers.join(helper, profile);
        TestPlayers.whenDone(helper, () -> {
            TestPlayers.leave(helper, profile);
            SENT.remove(profile.id());
        });
        return player;
    }

    private static void award(GameTestHelper helper, ServerPlayer player, String id) {
        player.getAdvancements().award(advancement(helper, record.advancementId(id)), id);
    }

    private static AdvancementHolder advancement(GameTestHelper helper, Identifier id) {
        var advancement = helper.getLevel().getServer().getAdvancements().get(id);
        if (advancement == null) {
            throw helper.assertionException(Component.literal("no advancement " + id));
        }
        return advancement;
    }

    private static void expectSent(GameTestHelper helper, ServerPlayer player, Discovery discovery, List<Set<String>> expected) {
        var sent = SENT.getOrDefault(player.getUUID(), List.of()).stream()
                .filter(payload -> payload.namespace().equals(discovery.namespace()))
                .map(KnownPayload::known)
                .toList();
        if (!sent.equals(expected)) {
            helper.fail(discovery.namespace() + " sent " + sent + ", expected " + expected);
        }
    }
}
