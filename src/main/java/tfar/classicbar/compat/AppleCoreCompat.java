package tfar.classicbar.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;
import tfar.classicbar.ClassicBar;

/**
 * Optional AppleCore integration. AppleCore (the GTNH "unofficial" fork) changes
 * food values through events, syncs saturation/exhaustion itself and lets mods
 * change the maximum exhaustion, so ClassicBar reads those values from its API
 * when the mod is present. Reflection is used to keep AppleCore optional.
 */
public final class AppleCoreCompat {

    private static boolean initialized;
    private static boolean available;
    private static Object accessor;
    private static Method isFood;
    private static Method getFoodValuesForPlayer;
    private static Method getExhaustion;
    private static Method getMaxExhaustion;
    private static Method getSaturationIncrement;
    private static Field foodValuesHunger;

    private AppleCoreCompat() {}

    private static void init() {
        if (initialized) return;
        initialized = true;
        if (!Loader.isModLoaded("AppleCore")) return;
        try {
            Class<?> api = Class.forName("squeek.applecore.api.AppleCoreAPI");
            Object acc = api.getField("accessor").get(null);
            if (acc == null) {
                // AppleCore has not initialized its API yet, retry on the next call
                initialized = false;
                return;
            }
            accessor = acc;
            Class<?> accessorInterface = Class.forName("squeek.applecore.api.IAppleCoreAccessor");
            isFood = accessorInterface.getMethod("isFood", ItemStack.class);
            getFoodValuesForPlayer = accessorInterface
                    .getMethod("getFoodValuesForPlayer", ItemStack.class, EntityPlayer.class);
            getExhaustion = accessorInterface.getMethod("getExhaustion", EntityPlayer.class);
            getMaxExhaustion = accessorInterface.getMethod("getMaxExhaustion", EntityPlayer.class);
            Class<?> foodValues = Class.forName("squeek.applecore.api.food.FoodValues");
            foodValuesHunger = foodValues.getField("hunger");
            getSaturationIncrement = foodValues.getMethod("getSaturationIncrement");
            available = true;
            ClassicBar.LOG.info("AppleCore detected: using its food values and saturation/exhaustion data.");
        } catch (Throwable t) {
            ClassicBar.LOG.warn("Could not hook into AppleCore", t);
        }
    }

    public static boolean available() {
        init();
        return available;
    }

    public static boolean isFood(ItemStack stack) {
        if (!available() || stack == null) return false;
        try {
            return (Boolean) isFood.invoke(accessor, stack);
        } catch (Throwable t) {
            return false;
        }
    }

    private static Object foodValues(EntityPlayer player, ItemStack stack) {
        if (!available() || stack == null) return null;
        try {
            return getFoodValuesForPlayer.invoke(accessor, stack, player);
        } catch (Throwable t) {
            return null;
        }
    }

    /** Hunger points the held food would restore (AppleCore aware). */
    public static int getHunger(EntityPlayer player, ItemStack stack) {
        Object values = foodValues(player, stack);
        if (values == null) return 0;
        try {
            return foodValuesHunger.getInt(values);
        } catch (Throwable t) {
            return 0;
        }
    }

    /** Saturation the held food would restore (AppleCore aware). */
    public static float getSaturationIncrement(EntityPlayer player, ItemStack stack) {
        Object values = foodValues(player, stack);
        if (values == null) return 0F;
        try {
            return (Float) getSaturationIncrement.invoke(values);
        } catch (Throwable t) {
            return 0F;
        }
    }

    public static float getExhaustion(EntityPlayer player) {
        if (!available()) return -1F;
        try {
            return (Float) getExhaustion.invoke(accessor, player);
        } catch (Throwable t) {
            return -1F;
        }
    }

    public static float getMaxExhaustion(EntityPlayer player) {
        if (!available()) return 4F;
        try {
            return (Float) getMaxExhaustion.invoke(accessor, player);
        } catch (Throwable t) {
            return 4F;
        }
    }
}
