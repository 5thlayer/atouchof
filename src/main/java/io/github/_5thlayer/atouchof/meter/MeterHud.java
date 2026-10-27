// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.meter;

import io.github._5thlayer.atouchof.ATouchOf;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * Draws the meters the client was told of, each as one more row of vanilla's right-hand column: above
 * the food bar or a mount's hearts, and below the air bubbles, which each row pushes up as the food bar
 * does. The rows stack in the order of their ids, and show only when vanilla's survival bars do.
 */
@EventBusSubscriber(modid = ATouchOf.MOD_ID, value = Dist.CLIENT)
final class MeterHud {

    private static final Identifier LAYER = Identifier.fromNamespaceAndPath(ATouchOf.MOD_ID, "meters");
    private static final Identifier CHEVRON_UP = Identifier.fromNamespaceAndPath(ATouchOf.MOD_ID, "meter/chevron_up");
    private static final Identifier CHEVRON_DOWN = Identifier.fromNamespaceAndPath(ATouchOf.MOD_ID, "meter/chevron_down");

    private static final int ICON = 9;
    private static final int ICON_STEP = 8;
    private static final int ROW_STEP = 10;
    private static final int CHEVRON_WIDTH = 7;
    private static final int CHEVRON_HEIGHT = 5;
    /** Each chevron overlaps the last by its outline. */
    private static final int CHEVRON_STEP = 6;

    private MeterHud() {
    }

    @SubscribeEvent
    static void registerLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.VEHICLE_HEALTH, LAYER, MeterHud::draw);
    }

    @SubscribeEvent
    static void loggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientMeters.forget();
    }

    private static void draw(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        var minecraft = Minecraft.getInstance();
        // Vanilla's own condition for its survival bars.
        if (minecraft.options.hideGui || minecraft.gameMode == null || !minecraft.gameMode.canHurtPlayer()) {
            return;
        }
        var gui = minecraft.gui;
        var xRight = graphics.guiWidth() / 2 + 91;
        for (var meter : Meter.all()) {
            var shown = ClientMeters.of(meter.id());
            // A state a newer server knows and this client's Pillar doesn't is not drawn.
            if (shown == null || shown.state() >= meter.states().size()) {
                continue;
            }
            var y = graphics.guiHeight() - gui.rightHeight;
            drawRow(graphics, meter.states().get(shown.state()), shown.halves(), xRight, y);
            drawChevrons(graphics, shown.trend(), xRight, y);
            gui.rightHeight += ROW_STEP;
        }
    }

    /** As vanilla's food bar: right to left, each place empty first, then full or half over it. */
    private static void drawRow(GuiGraphicsExtractor graphics, MeterState state, int halves, int xRight, int y) {
        for (var place = 0; place < MeterRow.ICONS; place++) {
            var x = xRight - place * ICON_STEP - ICON;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, state.empty(), x, y, ICON, ICON);
            switch (MeterRow.icon(halves, place)) {
                case FULL -> graphics.blitSprite(RenderPipelines.GUI_TEXTURED, state.full(), x, y, ICON, ICON);
                case HALF -> graphics.blitSprite(RenderPipelines.GUI_TEXTURED, state.half(), x, y, ICON, ICON);
                case EMPTY -> {
                }
            }
        }
    }

    /** The trend's chevrons, just right of the row, outside vanilla's column so they cover no icon. */
    private static void drawChevrons(GuiGraphicsExtractor graphics, int trend, int xRight, int y) {
        var sprite = trend > 0 ? CHEVRON_UP : CHEVRON_DOWN;
        var top = y + (ICON - CHEVRON_HEIGHT) / 2;
        for (var i = 0; i < MeterRow.chevrons(trend); i++) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, xRight + 1 + i * CHEVRON_STEP, top, CHEVRON_WIDTH, CHEVRON_HEIGHT);
        }
    }
}
