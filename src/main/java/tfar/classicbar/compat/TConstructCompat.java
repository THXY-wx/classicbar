package tfar.classicbar.compat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tfar.classicbar.ClassicBar;

/**
 * Tinkers' Construct draws the whole player health HUD itself (colored hearts,
 * including the extra hearts above 20 HP) from a RenderGameOverlayEvent.Pre
 * handler, and it sets the event canceled instead of checking the cancellation
 * flag, so cancelling the event ourselves cannot stop it.
 *
 * ClassicBar replaces that HUD, so the Tinkers renderer is unregistered from the
 * Forge event bus once all mods have initialized. Tinkers itself stays fully
 * installed and functional (hearts, canisters, armor, etc.).
 */
@SideOnly(Side.CLIENT)
public final class TConstructCompat {

    private static final String HEALTH_BAR_RENDERER = "tconstruct.client.HealthBarRenderer";

    private TConstructCompat() {}

    public static void disableTinkersHealthBar() {
        if (!Loader.isModLoaded("TConstruct")) return;
        int removed = 0;
        try {
            Field listenersField = EventBus.class.getDeclaredField("listeners");
            listenersField.setAccessible(true);
            Object value = listenersField.get(MinecraftForge.EVENT_BUS);
            if (!(value instanceof Map)) return;

            for (Object listenerOwner : new ArrayList<>(((Map<?, ?>) value).keySet())) {
                if (listenerOwner != null && HEALTH_BAR_RENDERER.equals(listenerOwner.getClass().getName())) {
                    MinecraftForge.EVENT_BUS.unregister(listenerOwner);
                    removed++;
                    ClassicBar.LOG.info(
                            "Disabled TConstruct's health bar renderer; ClassicBar now draws the health HUD.");
                }
            }
        } catch (Throwable t) {
            ClassicBar.LOG.warn("Could not disable TConstruct's health bar renderer", t);
            writeTestMarker("exception: " + t);
            return;
        }
        writeTestMarker("removed=" + removed);
    }

    private static void writeTestMarker(String text) {
        try {
            java.io.File flag = new java.io.File("classicbar-test.flag");
            if (flag.exists()) {
                java.nio.file.Files.write(
                        java.nio.file.Paths.get("classicbar-tconstruct-compat.txt"),
                        (text + System.lineSeparator()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
        } catch (Throwable ignored) {}
    }
}
