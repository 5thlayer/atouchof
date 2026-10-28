// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.gametest;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.github._5thlayer.atouchof.meter.Meter;
import io.github._5thlayer.atouchof.meter.MeterState;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Two meters to look at in the dev client, drawn with vanilla's own sprites, since the Runtime has no
 * meter of its own. {@code /atouchof_meters} turns them on and off for the player who runs it: the
 * first sweeps its row, its three states and every trend band, and is cleared for 2 s of every 16, so
 * the second, half full and still above it, drops into its place.
 */
final class DevMeters {

    private static final Set<UUID> SHOWN = ConcurrentHashMap.newKeySet();

    private static Meter sweeping;
    private static Meter still;

    private DevMeters() {
    }

    static void register() {
        sweeping = Meter.register(ATouchOfGameTests.id("dev_sweeping"), 20,
                state("hud/food_full", "hud/food_half", "hud/food_empty"),
                state("hud/food_full_hunger", "hud/food_half_hunger", "hud/food_empty_hunger"),
                state("hud/armor_full", "hud/armor_half", "hud/armor_empty"));
        still = Meter.register(ATouchOfGameTests.id("dev_still"), 20,
                state("hud/heart/full", "hud/heart/half", "hud/heart/container"));
        NeoForge.EVENT_BUS.addListener(DevMeters::commands);
        NeoForge.EVENT_BUS.addListener(DevMeters::tick);
    }

    private static MeterState state(String full, String half, String empty) {
        return new MeterState(Identifier.withDefaultNamespace(full), Identifier.withDefaultNamespace(half), Identifier.withDefaultNamespace(empty));
    }

    private static void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("atouchof_meters").executes(context -> {
            var player = context.getSource().getPlayerOrException();
            var on = SHOWN.add(player.getUUID()) || !SHOWN.remove(player.getUUID());
            if (!on) {
                sweeping.clear(player);
                still.clear(player);
            }
            context.getSource().sendSuccess(() -> Component.literal("Dev meters " + (on ? "on" : "off")), false);
            return 1;
        }));
    }

    private static void tick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !SHOWN.contains(player.getUUID())) {
            return;
        }
        // One half step every half second, across the row and back; the state and band move every 2 s.
        var ticks = player.tickCount;
        var step = (ticks / 10) % 40;
        if ((ticks / 40) % 8 == 7) {
            sweeping.clear(player);
        } else {
            sweeping.set(player, step <= 20 ? step : 40 - step, (ticks / 40) % 3, (ticks / 40) % 7 - 3);
        }
        still.set(player, 10, 0, 0);
    }
}
