// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.discovery;

import java.util.HashSet;
import java.util.Set;

import io.github._5thlayer.atouchof.ATouchOf;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** What the server tells a player's client is known of one Pillar's things: the ids its {@link Discovery#isKnown} answers known for. */
public record KnownPayload(String namespace, Set<String> known) implements CustomPacketPayload {

    static final Type<KnownPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ATouchOf.MOD_ID, "known"));

    static final StreamCodec<ByteBuf, KnownPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, KnownPayload::namespace,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(HashSet::new)), payload -> new HashSet<>(payload.known()),
            KnownPayload::new);

    public KnownPayload {
        known = Set.copyOf(known);
    }

    @Override
    public Type<KnownPayload> type() {
        return TYPE;
    }
}
