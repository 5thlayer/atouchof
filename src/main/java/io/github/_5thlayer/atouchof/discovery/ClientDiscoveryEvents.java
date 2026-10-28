// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.discovery;

import io.github._5thlayer.atouchof.ATouchOf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/** The client's own events for {@link ClientDiscovery}, apart from it, which a server loads too. */
@EventBusSubscriber(modid = ATouchOf.MOD_ID, value = Dist.CLIENT)
final class ClientDiscoveryEvents {

    private ClientDiscoveryEvents() {
    }

    @SubscribeEvent
    static void loggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientDiscovery.forget();
    }
}
