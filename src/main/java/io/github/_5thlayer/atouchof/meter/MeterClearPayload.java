// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.meter;

import io.github._5thlayer.atouchof.ATouchOf;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** The server telling a player's client to stop drawing one meter, until it is told of it again. */
public record MeterClearPayload(Identifier meter) implements CustomPacketPayload {

    static final Type<MeterClearPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ATouchOf.MOD_ID, "meter_clear"));

    static final StreamCodec<ByteBuf, MeterClearPayload> STREAM_CODEC =
            Identifier.STREAM_CODEC.map(MeterClearPayload::new, MeterClearPayload::meter);

    @Override
    public Type<MeterClearPayload> type() {
        return TYPE;
    }
}
