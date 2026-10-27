// SPDX-FileCopyrightText: 2026 5thlayer
// SPDX-License-Identifier: LGPL-3.0-only

package io.github._5thlayer.atouchof.gametest;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.serialization.MapCodec;
import io.github._5thlayer.atouchof.ATouchOf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The Runtime's game tests, run by the {@code gameTestServer} Gradle run: the Minecraft-integration
 * seam, with a real player on a real server. Each test stands on the {@code gametest/platform}
 * structure that the plugin's {@code buildGametestStructures} task writes, and sets up what it needs
 * itself. It lives in the {@code gametest} source set, which only the Runtime's own runs load, and
 * hooks itself in, so the {@code @Mod} class names no game-test code (libworks ADR 0011).
 */
@EventBusSubscriber(modid = ATouchOf.MOD_ID)
public final class ATouchOfGameTests {

    private static final Identifier PLATFORM = id("gametest/platform");

    private static final DeferredRegister<MapCodec<? extends GameTestInstance>> TEST_TYPES =
            DeferredRegister.create(Registries.TEST_INSTANCE_TYPE, ATouchOf.MOD_ID);

    static {
        TEST_TYPES.register("code", () -> CodeGameTest.CODEC);
    }

    private ATouchOfGameTests() {
    }

    @SubscribeEvent
    static void construct(FMLConstructModEvent event) {
        register(event.getContainer().getEventBus());
    }

    public static void register(IEventBus modBus) {
        TEST_TYPES.register(modBus);
        MeterSyncTests.listen();
        DevMeters.register();
        // Posted only when game tests are enabled, which a production game never is.
        modBus.addListener(ATouchOfGameTests::registerTests);
    }

    private static void registerTests(RegisterGameTestsEvent event) {
        var tests = new Registrar(event, event.registerEnvironment(id("default"), new TestEnvironmentDefinition.AllOf(List.of())));
        MeterSyncTests.register(tests);
        StrikeTests.register(tests);
    }

    static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ATouchOf.MOD_ID, path);
    }

    /** What a test class is handed: a name, a tick budget and a body per test. */
    record Registrar(RegisterGameTestsEvent event, Holder<TestEnvironmentDefinition<?>> environment) {

        void test(String name, int maxTicks, Consumer<GameTestHelper> body) {
            var id = id(name);
            CodeGameTest.define(id, body);
            event.registerTest(id, new CodeGameTest(id, new TestData<>(environment, PLATFORM, maxTicks, 0, true, Rotation.NONE,
                    false, 1, 1, false, 0)));
        }
    }
}
