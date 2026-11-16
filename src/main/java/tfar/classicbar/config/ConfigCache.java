package tfar.classicbar.config;

import java.util.ArrayList;
import java.util.List;

import tfar.classicbar.util.Color;
import tfar.classicbar.util.ColorUtils;

public class ConfigCache {

    public static boolean icons;

    public static final List<Color> armor = new ArrayList<>();
    public static final List<Color> absorption = new ArrayList<>();
    public static final List<Color> healthLayers = new ArrayList<>();

    public static final List<Color> normal = new ArrayList<>();
    public static final List<Color> poison = new ArrayList<>();
    public static final List<Color> wither = new ArrayList<>();

    public static final List<Double> normalFractions = new ArrayList<>();
    public static final List<Double> poisonFractions = new ArrayList<>();
    public static final List<Double> witherFractions = new ArrayList<>();

    public static Color hunger;
    public static Color hungerDebuff;
    public static Color saturation;
    public static Color saturationDebuff;
    public static Color air;

    private static void clear() {
        armor.clear();
        absorption.clear();
        healthLayers.clear();
        normal.clear();
        poison.clear();
        wither.clear();
        normalFractions.clear();
        poisonFractions.clear();
        witherFractions.clear();
    }

    public static void bake() {
        clear();
        icons = ClassicBarsConfig.displayIcons;

        cacheList(ClassicBarsConfig.armorColors, armor);
        cacheList(ClassicBarsConfig.absorptionColors, absorption);
        cacheList(ClassicBarsConfig.healthLayerColors, healthLayers);

        cacheList(ClassicBarsConfig.normalColors, normal);
        cacheList(ClassicBarsConfig.poisonedColors, poison);
        cacheList(ClassicBarsConfig.witheredColors, wither);

        cacheFractions(ClassicBarsConfig.normalFractions, normalFractions);
        cacheFractions(ClassicBarsConfig.poisonedFractions, poisonFractions);
        cacheFractions(ClassicBarsConfig.witheredFractions, witherFractions);

        hunger = ColorUtils.hex2Color(ClassicBarsConfig.hungerBarColor);
        hungerDebuff = ColorUtils.hex2Color(ClassicBarsConfig.hungerBarDebuffColor);
        saturation = ColorUtils.hex2Color(ClassicBarsConfig.saturationBarColor);
        saturationDebuff = ColorUtils.hex2Color(ClassicBarsConfig.saturationBarDebuffColor);
        air = ColorUtils.hex2Color(ClassicBarsConfig.airBarColor);
    }

    private static void cacheList(String[] config, List<Color> cache) {
        for (String s : config) {
            cache.add(ColorUtils.hex2Color(s));
        }
    }

    private static void cacheFractions(String[] config, List<Double> cache) {
        for (String s : config) {
            try {
                cache.add(Double.parseDouble(s.trim()));
            } catch (NumberFormatException e) {
                cache.add(0D);
            }
        }
    }
}
