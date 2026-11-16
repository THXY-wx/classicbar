package tfar.classicbar.client;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tfar.classicbar.compat.AppleCoreCompat;
import tfar.classicbar.util.FoodStatsHelper;

/**
 * Holds the values that are not synchronised by vanilla 1.7.10 (saturation and
 * exhaustion) until the server sends them.
 */
@SideOnly(Side.CLIENT)
public final class ClientData {

    private static float saturation = -1F;
    private static float exhaustion = -1F;

    private ClientData() {}

    public static void setSaturation(float value) {
        saturation = value;
    }

    public static void setExhaustion(float value) {
        exhaustion = value;
    }

    public static boolean isSaturationSynced() {
        return saturation >= 0F || AppleCoreCompat.available();
    }

    public static boolean isExhaustionSynced() {
        return exhaustion >= 0F || AppleCoreCompat.available();
    }

    public static float getSaturation(EntityPlayer player) {
        // ClassicBar's own sync wins; otherwise FoodStats (AppleCore syncs into it when installed)
        return saturation >= 0F ? saturation : player.getFoodStats().getSaturationLevel();
    }

    public static float getExhaustion(EntityPlayer player) {
        if (exhaustion >= 0F) return exhaustion;
        // AppleCore syncs exhaustion into FoodStats itself
        if (AppleCoreCompat.available()) {
            float value = AppleCoreCompat.getExhaustion(player);
            if (value >= 0F) return value;
        }
        return FoodStatsHelper.getExhaustion(player.getFoodStats());
    }

    public static void reset() {
        saturation = -1F;
        exhaustion = -1F;
    }
}
