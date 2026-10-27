// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.meter;

import io.github._5thlayer.atouchof.ATouchOf;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** What the server tells a player's client of one meter: the half steps its row fills, its state and its trend band. */
public record MeterPayload(Identifier meter, int halves, int state, int trend) implements CustomPacketPayload {

    static final Type<MeterPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ATouchOf.MOD_ID, "meter"));

    static final StreamCodec<ByteBuf, MeterPayload> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, MeterPayload::meter,
            ByteBufCodecs.VAR_INT, MeterPayload::halves,
            ByteBufCodecs.VAR_INT, MeterPayload::state,
            ByteBufCodecs.VAR_INT, MeterPayload::trend,
            MeterPayload::new);

    @Override
    public Type<MeterPayload> type() {
        return TYPE;
    }
}
