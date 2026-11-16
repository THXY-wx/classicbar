package tfar.classicbar.util;

import org.lwjgl.opengl.GL11;

/**
 * Simple RGB color holder with helpers for the fixed pipeline used by 1.7.10.
 */
public class Color {

    public static final Color BLACK = Color.from(0, 0, 0);
    public static final Color RED = Color.from(0xff, 0, 0);
    public static final Color YELLOW = Color.from(0xff, 0xff, 0);

    public final int r;
    public final int g;
    public final int b;

    protected Color(int red, int green, int blue) {
        this.r = red;
        this.g = green;
        this.b = blue;
    }

    public static Color from(int red, int green, int blue) {
        return new Color(red, green, blue);
    }

    public static Color from(String s) {
        return ColorUtils.hex2Color(s);
    }

    public void color2Gl() {
        color2Gla(1);
    }

    public void color2Gla(float a) {
        GL11.glColor4f(this.r / 255f, this.g / 255f, this.b / 255f, a);
    }

    public Color colorBlend(Color c2, double d) {
        int r = (int) Math.floor(this.r * (1 - d) + c2.r * d);
        int g = (int) Math.floor(this.g * (1 - d) + c2.g * d);
        int b = (int) Math.floor(this.b * (1 - d) + c2.b * d);
        return Color.from(r, g, b);
    }

    public int colorToText() {
        return (this.r << 16) + (this.g << 8) + this.b;
    }

    public static void reset() {
        GL11.glColor4f(1, 1, 1, 1);
    }
}
