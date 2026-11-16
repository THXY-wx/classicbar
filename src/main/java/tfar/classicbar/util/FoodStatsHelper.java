package tfar.classicbar.util;

import net.minecraft.util.FoodStats;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

/**
 * 1.7.10 has no getter for the food exhaustion level, but the HUD needs it.
 * The field is private, so read it through Forge's deobfuscation-aware
 * reflection helper (works in both dev and production environments).
 */
public final class FoodStatsHelper {

    private static boolean unavailable;

    private FoodStatsHelper() {}

    public static float getExhaustion(FoodStats stats) {
        if (unavailable || stats == null) return 0F;
        try {
            Float value = ObfuscationReflectionHelper
                    .getPrivateValue(FoodStats.class, stats, "foodExhaustionLevel");
            return value == null ? 0F : value;
        } catch (Throwable t) {
            unavailable = true;
            return 0F;
        }
    }
}
