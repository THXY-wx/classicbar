package tfar.classicbar.util;

import java.util.List;

import tfar.classicbar.config.ConfigCache;

public class ColorUtils {

    public static Color hex2Color(String s) {
        int i1;
        try {
            i1 = Integer.decode(s);
        } catch (NumberFormatException e) {
            i1 = 0xFFFFFF;
        }
        int r = i1 >> 16 & 0xFF;
        int g = i1 >> 8 & 0xFF;
        int b = i1 & 0xFF;
        return Color.from(r, g, b);
    }

    public static Color calculateScaledColor(double d1, double d2, HealthEffect effect) {
        double d3 = d2 <= 0 ? 0 : d1 / d2;

        List<Color> colorCodes;
        List<Double> colorFractions;

        switch (effect) {
            case POISON:
                colorCodes = ConfigCache.poison;
                colorFractions = ConfigCache.poisonFractions;
                break;
            case WITHER:
                colorCodes = ConfigCache.wither;
                colorFractions = ConfigCache.witherFractions;
                break;
            case NONE:
            default:
                colorCodes = ConfigCache.normal;
                colorFractions = ConfigCache.normalFractions;
                break;
        }

        if (colorCodes.isEmpty() || colorCodes.size() != colorFractions.size()) return Color.BLACK;

        int i3 = 0;
        for (int i2 = 0; i2 < colorFractions.size() - 1; i2++) {
            if (d3 < colorFractions.get(i2)) break;
            i3++;
        }

        // return first color in the list if health is too low
        if (d3 <= colorFractions.get(0)) return colorCodes.get(0);
        // return last color in the list if health is too high
        if (d3 >= colorFractions.get(colorFractions.size() - 1)) return colorCodes.get(colorCodes.size() - 1);
        if (i3 <= 0) return colorCodes.get(0);

        Color c1 = colorCodes.get(i3 - 1);
        Color c2 = colorCodes.get(i3);

        double lo = colorFractions.get(i3 - 1);
        double hi = colorFractions.get(i3);
        double d4 = hi == lo ? 0 : (d3 - lo) / (hi - lo);
        return c1.colorBlend(c2, d4);
    }
}
