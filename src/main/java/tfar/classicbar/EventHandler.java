package tfar.classicbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.config.ClassicBarsConfig;

@SideOnly(Side.CLIENT)
public class EventHandler {

    private static final List<BarOverlay> all = new ArrayList<>();
    private static final Map<String, BarOverlay> registry = new HashMap<>();
    private static final List<BarOverlay> errored = new ArrayList<>();

    private static int leftHeight;
    private static int rightHeight;

    public static void register(BarOverlay overlay) {
        if (overlay != null && overlay.name() != null) {
            registry.put(overlay.name(), overlay);
        }
    }

    public static void registerAll(BarOverlay... overlays) {
        Arrays.stream(overlays)
                .forEach(EventHandler::register);
    }

    @SubscribeEvent
    public void onPreRender(RenderGameOverlayEvent.Pre event) {
        // keep the vanilla bars hidden even while a GUI is open, so opening the
        // chat window does not suddenly show the old hearts again
        if (!shouldHideVanillaBars()) return;
        switch (event.type) {
            case HEALTH:
            case ARMOR:
            case FOOD:
            case AIR:
            case HEALTHMOUNT:
                event.setCanceled(true);
                break;
            default:
                break;
        }
    }

    @SubscribeEvent
    public void onRenderPost(RenderGameOverlayEvent.Post event) {
        // GuiIngameForge draws the chat AFTER the hotbar/experience, so drawing at
        // POST(HOTBAR) puts the bars below the chat window (like the vanilla
        // hearts) instead of on top of the chat text.
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return;
        if (!shouldRenderHud()) return;

        leftHeight = 39;
        rightHeight = 39;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(1, 1, 1, 1);

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        for (BarOverlay overlay : all) {
            if (errored.contains(overlay)) continue;
            try {
                overlay.render(event.resolution, player, getOffset(overlay.rightHandSide()));
            } catch (Throwable t) {
                ClassicBar.LOG.error("Removing broken overlay " + overlay.name(), t);
                errored.add(overlay);
            }
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private static boolean shouldHideVanillaBars() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.gameSettings.hideGUI) return false;
        if (mc.playerController == null || !mc.playerController.shouldDrawHUD()) return false;
        return !mc.thePlayer.capabilities.isCreativeMode;
    }

    private static boolean shouldRenderHud() {
        return shouldHideVanillaBars();
    }

    public static void increment(boolean side, int amount) {
        if (side) rightHeight += amount;
        else leftHeight += amount;
    }

    public static int getOffset(boolean right) {
        return right ? rightHeight : leftHeight;
    }

    public static void cacheConfigs() {
        all.clear();
        for (String name : ClassicBarsConfig.leftOrder) {
            BarOverlay overlay = registry.get(name);
            if (overlay != null) all.add(overlay.setSide(false));
        }
        for (String name : ClassicBarsConfig.rightOrder) {
            BarOverlay overlay = registry.get(name);
            if (overlay != null) all.add(overlay.setSide(true));
        }
        all.removeAll(errored);
    }
}
