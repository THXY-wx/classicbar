package tfar.classicbar.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import tfar.classicbar.impl.BarOverlayImpl;

public final class ModUtils {

    private ModUtils() {}

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    /**
     * Draws a textured rectangle using the currently bound texture. Unlike
     * {@code Gui#drawTexturedModalRect} this accepts fractional coordinates,
     * which is required for smooth bar widths.
     */
    public static void drawTexturedModalRect(double x, double y, int u, int v, double width, int height) {
        final double tex = 1D / 256D;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0, u * tex, (v + height) * tex);
        tessellator.addVertexWithUV(x + width, y + height, 0, (u + width) * tex, (v + height) * tex);
        tessellator.addVertexWithUV(x + width, y, 0, (u + width) * tex, v * tex);
        tessellator.addVertexWithUV(x, y, 0, u * tex, v * tex);
        tessellator.draw();
    }

    public static double getWidth(double value, double max) {
        if (max <= 0) return 0;
        return Math.ceil(BarOverlayImpl.WIDTH * value / max);
    }

    public static int getStringLength(String s) {
        return mc().fontRenderer.getStringWidth(s);
    }

    public static void drawStringOnHUD(String string, int x, int y, int color) {
        mc().fontRenderer.drawStringWithShadow(string, x + 2, y + 2, color);
    }
}
