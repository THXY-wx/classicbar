package tfar.classicbar.config;

import java.io.File;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import tfar.classicbar.ClassicBar;

/**
 * 1.7.10 port of the ClassicBar configuration. The values mirror the upstream
 * (1.19.x) config file as closely as the 1.7.10 config system allows.
 */
public class ClassicBarsConfig {

    public static Configuration config;

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_COLORS = "colors";
    public static final String CATEGORY_ORDER = "order";

    public static boolean displayIcons = true;
    public static boolean fullAbsorptionBar = false;
    public static boolean fullArmorBar = false;
    public static boolean lowArmorWarning = true;

    public static boolean showSaturationBar = true;
    public static boolean showHeldFoodOverlay = true;
    public static boolean showExhaustionOverlay = true;

    public static boolean showAbsorptionNumbers = true;
    public static boolean showAirNumbers = true;
    public static boolean showArmorNumbers = true;
    public static boolean showHealthNumbers = true;
    public static boolean showHungerNumbers = true;
    public static boolean showMountHealthNumbers = true;

    /**
     * Health bar style when max health is above 20:
     * single = one color, layered = one color per 20 HP segment, overlap = higher
     * layers painted on top of the base layer (like Tinkers' colored hearts).
     */
    public static String healthLayerMode = "layered";
    /** Width in HP of the blended zone around every layer boundary (0 = hard edge). */
    public static double healthLayerGradient = 16.0D;
    public static String[] healthLayerColors = { "#FF3B3B", "#FFD24A", "#5BE65B", "#4AC8FF", "#C77CFF", "#FF8ACF",
            "#FFFFFF", "#FF9A3B", "#9BFF6B", "#6B8CFF" };

    public static float transitionSpeed = 3F;

    public static String hungerBarColor = "#B34D00";
    public static String hungerBarDebuffColor = "#249016";
    public static String saturationBarColor = "#FFCC00";
    public static String saturationBarDebuffColor = "#87BC00";
    public static String airBarColor = "#00E6E6";

    public static String[] armorColors = { "#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF" };
    public static String[] absorptionColors = { "#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2",
            "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF" };

    public static String[] normalColors = { "#FF0000", "#FFFF00", "#00FF00" };
    public static String[] normalFractions = { "0.25", "0.5", "0.75" };
    public static String[] poisonedColors = { "#00FF00", "#55FF55", "#00FF00" };
    public static String[] poisonedFractions = { "0.25", "0.5", "0.75" };
    public static String[] witheredColors = { "#555555", "#AAAAAA", "#555555" };
    public static String[] witheredFractions = { "0.25", "0.5", "0.75" };

    public static String[] leftOrder = { "health", "armor", "absorption" };
    public static String[] rightOrder = { "health_mount", "food", "air" };

    public static void init(File configFile) {
        config = new Configuration(configFile);
        load();
    }

    /** Re-reads the values from the in-memory Configuration (used by the config GUI). */
    public static void load() {
        if (config == null) return;

        config.addCustomCategoryComment(
                CATEGORY_GENERAL,
                "Toggle and tweak the ClassicBar HUD. Changes from the config GUI are applied immediately.");

        displayIcons = config.getBoolean("display_icons", CATEGORY_GENERAL, displayIcons, "Draw an icon next to every bar.");
        fullAbsorptionBar = config.getBoolean(
                "full_absorption_bar",
                CATEGORY_GENERAL,
                fullAbsorptionBar,
                "Always draw the full width absorption bar background.");
        fullArmorBar = config.getBoolean(
                "full_armor_bar",
                CATEGORY_GENERAL,
                fullArmorBar,
                "Always draw the full width armor bar background.");
        lowArmorWarning = config.getBoolean(
                "display_low_armor_warning",
                CATEGORY_GENERAL,
                lowArmorWarning,
                "Flash the armor bar when a piece is about to break.");

        showSaturationBar = config.getBoolean(
                "show_saturation_bar",
                CATEGORY_GENERAL,
                showSaturationBar,
                "Draw the saturation overlay on the hunger bar.");
        showHeldFoodOverlay = config.getBoolean(
                "show_held_food_overlay",
                CATEGORY_GENERAL,
                showHeldFoodOverlay,
                "Preview how much hunger/saturation the held food restores.");
        showExhaustionOverlay = config.getBoolean(
                "show_exhaustion_overlay",
                CATEGORY_GENERAL,
                showExhaustionOverlay,
                "Draw the exhaustion overlay on the hunger bar (requires the mod on the server).");
        transitionSpeed = config.getFloat(
                "transition_speed",
                CATEGORY_GENERAL,
                transitionSpeed,
                0F,
                20F,
                "Pulse speed of the held food overlay.");

        showAbsorptionNumbers = config.getBoolean("show_absorption_numbers", CATEGORY_GENERAL, showAbsorptionNumbers, "Draw absorption numbers.");
        showAirNumbers = config.getBoolean("show_air_numbers", CATEGORY_GENERAL, showAirNumbers, "Draw air numbers.");
        showArmorNumbers = config.getBoolean("show_armor_numbers", CATEGORY_GENERAL, showArmorNumbers, "Draw armor numbers.");
        showHealthNumbers = config.getBoolean("show_health_numbers", CATEGORY_GENERAL, showHealthNumbers, "Draw health numbers.");
        showHungerNumbers = config.getBoolean("show_hunger_numbers", CATEGORY_GENERAL, showHungerNumbers, "Draw hunger numbers.");
        showMountHealthNumbers = config.getBoolean("show_mount_health_numbers", CATEGORY_GENERAL, showMountHealthNumbers, "Draw mount health numbers.");

        healthLayerMode = config.getString(
                "health_layer_mode",
                CATEGORY_GENERAL,
                healthLayerMode,
                "Health bar style when max health is above 20: single = one color (the old gradient), layered = one color per 20 HP segment, overlap = higher layers painted over the base layer like Tinkers' colored hearts.");
        healthLayerGradient = config.getFloat(
                "health_layer_gradient",
                CATEGORY_GENERAL,
                (float) healthLayerGradient,
                0F,
                20F,
                "Width of the blended zone (in health points) where two health layers meet. 0 = hard color edge.");

        config.addCustomCategoryComment(CATEGORY_COLORS, "Colors are hexadecimal RGB values, e.g. #FF0000.");

        hungerBarColor = config.getString("hunger_bar_color", CATEGORY_COLORS, hungerBarColor, "Hunger bar color.");
        hungerBarDebuffColor = config.getString("hunger_bar_debuff_color", CATEGORY_COLORS, hungerBarDebuffColor, "Hunger bar color while the hunger effect is active.");
        saturationBarColor = config.getString("saturation_bar_color", CATEGORY_COLORS, saturationBarColor, "Saturation bar color.");
        saturationBarDebuffColor = config.getString("saturation_bar_debuff_color", CATEGORY_COLORS, saturationBarDebuffColor, "Saturation bar color while the hunger effect is active.");
        airBarColor = config.getString("air_bar_color", CATEGORY_COLORS, airBarColor, "Air bar color.");

        armorColors = config.getStringList("armor_color_values", CATEGORY_COLORS, armorColors, "One color per 20 armor points, the last one is used for the rest.");
        absorptionColors = config.getStringList("absorption_color_values", CATEGORY_COLORS, absorptionColors, "One color per 20 absorption points, the last one is used for the rest.");

        normalColors = config.getStringList("normal_colors", CATEGORY_COLORS, normalColors, "Health bar colors (low to high).");
        normalFractions = config.getStringList("normal_fractions", CATEGORY_COLORS, normalFractions, "Health fractions at which the colors above change (low to high).");
        healthLayerColors = config.getStringList(
                "health_layer_colors",
                CATEGORY_COLORS,
                healthLayerColors,
                "Health bar color for each 20 HP layer when max health is above 20. The last color is reused for further layers.");
        poisonedColors = config.getStringList("poisoned_colors", CATEGORY_COLORS, poisonedColors, "Health bar colors while poisoned.");
        poisonedFractions = config.getStringList("poisoned_fractions", CATEGORY_COLORS, poisonedFractions, "Health fractions while poisoned.");
        witheredColors = config.getStringList("withered_colors", CATEGORY_COLORS, witheredColors, "Health bar colors while withered.");
        witheredFractions = config.getStringList("withered_fractions", CATEGORY_COLORS, witheredFractions, "Health fractions while withered.");

        config.addCustomCategoryComment(CATEGORY_ORDER, "Order of the bars from the bottom of the screen upwards.");
        leftOrder = config.getStringList("left_order", CATEGORY_ORDER, leftOrder, "Bars drawn on the left side, bottom to top.");
        rightOrder = config.getStringList("right_order", CATEGORY_ORDER, rightOrder, "Bars drawn on the right side, bottom to top.");

        if (config.hasChanged()) {
            config.save();
        }

        applyLanguageKeys();
        ConfigCache.bake();
        ClassicBar.LOG.info("Loaded ClassicBar configuration.");
    }

    /** Re-reads the config file from disk (used by /classicbar reload). */
    public static void reloadFromDisk() {
        if (config == null) return;
        config.load();
        load();
    }

    /**
     * 1.7.10's config GUI shows {@code Property#getLanguageKey()} (translated) when
     * available and falls back to the raw property name, so every category and
     * property gets a language key. Translations live in assets/classicbar/lang.
     */
    private static void applyLanguageKeys() {
        // option from the first 1.1.0 test build, replaced by health_layer_mode
        ConfigCategory general = config.getCategory(CATEGORY_GENERAL);
        if (general.containsKey("enable_health_layers")) {
            general.remove("enable_health_layers");
        }

        for (String categoryName : new String[] { CATEGORY_GENERAL, CATEGORY_COLORS, CATEGORY_ORDER }) {
            ConfigCategory category = config.getCategory(categoryName);
            category.setLanguageKey("classicbar.config." + categoryName);
            for (Property property : category.getOrderedValues()) {
                property.setLanguageKey("classicbar.config." + categoryName + "." + property.getName());
            }
        }
        if (config.hasChanged()) {
            config.save();
        }
    }
}
