// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.discovery;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * A Pillar's Discovery record (ADR-0006): each of its discoverable things is one advancement at
 * {@code <namespace>:discovery/<id>}, under {@code <namespace>:discovery/root}, and that advancement
 * being done is the Discovery. A missing one, as when a pack removed it, counts as known, and with the
 * Pillar's Discovery switched off everything does. The Runtime tells each player's client what
 * {@link #isKnown} answers for them on login, on each change to one of these advancements, on a
 * datapack reload and on a reload of the Pillar's config, whenever that differs from what it last told.
 *
 * <p>The record is all the Runtime keeps: what discovers a thing, being its advancement and the
 * triggers that grant it, and what the Discovery gates stay the Pillar's (ADR-0002). A Pillar registers
 * its record while the mods are constructed, on both sides, so its client can answer {@link #isKnownOnClient}.
 */
public final class Discovery {

    private static final String PREFIX = "discovery/";

    /** Every Pillar's record, by namespace. */
    private static final Map<String, Discovery> RECORDS = new ConcurrentSkipListMap<>();

    private static volatile boolean closed;

    private final String namespace;
    private final Set<String> ids;
    private final BooleanSupplier enabled;

    private Discovery(String namespace, Set<String> ids, BooleanSupplier enabled) {
        this.namespace = namespace;
        this.ids = ids;
        this.enabled = enabled;
    }

    /**
     * Registers a Pillar's record, from its mod constructor.
     *
     * @param modBus    the Pillar's mod bus, on whose config reloads the Runtime tells every client again
     * @param namespace the namespace of the Pillar's discovery advancements, its mod id
     * @param ids       each discoverable thing's id, its advancement being {@code <namespace>:discovery/<id>}
     * @param enabled   whether the Pillar's Discovery is switched on, asked on the server thread
     * @throws IllegalStateException    once the mods are set up, or for a namespace already registered
     * @throws IllegalArgumentException for a namespace or an id an advancement can't be named by, or the root's
     */
    public static Discovery register(IEventBus modBus, String namespace, Collection<String> ids, BooleanSupplier enabled) {
        Objects.requireNonNull(modBus, "modBus");
        Objects.requireNonNull(namespace, "namespace");
        Objects.requireNonNull(enabled, "enabled");
        if (closed) {
            throw new IllegalStateException("discovery " + namespace + " registered once the mods were set up; register it from the mod's constructor");
        }
        if (!Identifier.isValidNamespace(namespace)) {
            throw new IllegalArgumentException("discovery " + namespace + " is no namespace");
        }
        for (var id : ids) {
            if (id.equals("root") || !Identifier.isValidPath(PREFIX + id)) {
                throw new IllegalArgumentException("discovery " + namespace + " can't name a thing " + id);
            }
        }
        var discovery = new Discovery(namespace, Set.copyOf(ids), enabled);
        if (RECORDS.putIfAbsent(namespace, discovery) != null) {
            throw new IllegalStateException("discovery " + namespace + " is already registered");
        }
        modBus.addListener(ModConfigEvent.Loading.class, event -> discovery.retell());
        modBus.addListener(ModConfigEvent.Reloading.class, event -> discovery.retell());
        return discovery;
    }

    /**
     * Whether the player has discovered the thing: always, when the Pillar's Discovery is off or the
     * thing's advancement is missing; otherwise when its advancement is done.
     *
     * @throws IllegalArgumentException for an id the Pillar did not register
     */
    public boolean isKnown(ServerPlayer player, String id) {
        requireRegistered(id);
        if (!enabled.getAsBoolean()) {
            return true;
        }
        var advancement = player.level().getServer().getAdvancements().get(advancementId(id));
        return advancement == null || player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    /** The ids of every thing {@link #isKnown} answers known for, for the player: what their client is told. */
    public Set<String> known(ServerPlayer player) {
        return ids.stream().filter(id -> isKnown(player, id)).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Whether this client's player has discovered the thing, as the server last told it: for what the
     * client shows, cheap enough to ask every frame. Until the client is told, nothing is known.
     *
     * @throws IllegalArgumentException for an id the Pillar did not register
     */
    public boolean isKnownOnClient(String id) {
        requireRegistered(id);
        return ClientDiscovery.isKnown(namespace, id);
    }

    /** The advancement a thing's id stands for, or the root's for {@code root}. */
    public Identifier advancementId(String id) {
        return Identifier.fromNamespaceAndPath(namespace, PREFIX + id);
    }

    /** The root of the Pillar's discovery advancements. */
    public Identifier root() {
        return advancementId("root");
    }

    public String namespace() {
        return namespace;
    }

    /** Every discoverable thing's id. */
    public Set<String> ids() {
        return ids;
    }

    /**
     * Tells every player's client what is known again, where it changed, as the Runtime does on a reload
     * of the Pillar's config. A Pillar calls it when its Discovery is switched on or off some other way.
     * From any thread; with no server running, it does nothing.
     */
    public void retell() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        Runnable retell = () -> server.getPlayerList().getPlayers().forEach(player -> DiscoverySync.tell(player, this));
        if (server.isSameThread()) {
            retell.run();
        } else {
            server.execute(retell);
        }
    }

    /** Every Pillar's record. */
    static Collection<Discovery> all() {
        return RECORDS.values();
    }

    /** The record whose discovery advancements include this one, or null. */
    static Discovery owning(Identifier advancement) {
        return advancement.getPath().startsWith(PREFIX) ? RECORDS.get(advancement.getNamespace()) : null;
    }

    /** Refuses any registration from now on, as the mods are set up. */
    static void close() {
        closed = true;
    }

    private void requireRegistered(String id) {
        if (!ids.contains(id)) {
            throw new IllegalArgumentException("discovery " + namespace + " has no thing " + id);
        }
    }
}
