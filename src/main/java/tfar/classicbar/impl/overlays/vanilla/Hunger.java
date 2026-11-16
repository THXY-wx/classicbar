package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import org.lwjgl.opengl.GL11;

import tfar.classicbar.ClassicBar;
import tfar.classicbar.client.ClientData;
import tfar.classicbar.compat.AppleCoreCompat;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class Hunger extends BarOverlayImpl {

    public Hunger() {
        super("food");
    }

    @Override
    public boolean shouldRender(EntityPlayer player) {
        // 1.7.10 replaces the hunger bar with the mount health bar while riding
        return player.ridingEntity == null;
    }

    @Override
    public void renderBar(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        double hunger = player.getFoodStats().getFoodLevel();
        double maxHunger = 20;

        double barWidthH = getBarWidth(player);

        double currentSat = ClientData.getSaturation(player);
        double maxSat = maxHunger;
        double barWidthS = getSatBarWidth(player);
        float exhaustion = ClientData.getExhaustion(player);

        int xStart = resolution.getScaledWidth() / 2 + getHOffset();
        int yStart = resolution.getScaledHeight() - vOffset;

        // Bar background
        Color.reset();
        renderFullBarBackground(xStart, yStart);
        // draw portion of bar based on hunger amount
        double f = xStart + (rightHandSide() ? WIDTH - barWidthH : 0);

        Color hungerColor = getSecondaryBarColor(0, player);
        Color satColor = getPrimaryBarColor(0, player);

        hungerColor.color2Gl();
        renderPartialBar(f + 2, yStart + 2, barWidthH);
        if (currentSat > 0 && ClassicBarsConfig.showSaturationBar) {
            // draw saturation
            satColor.color2Gl();
            f = xStart + (rightHandSide() ? WIDTH - barWidthS : 0);
            renderPartialBar(f + 2, yStart + 2, barWidthS);
        }
        // render held food overlay
        ItemStack held = player.getCurrentEquippedItem();
        boolean appleCoreFood = held != null && AppleCoreCompat.available() && AppleCoreCompat.isFood(held);
        if (ClassicBarsConfig.showHeldFoodOverlay
                && (appleCoreFood || (held != null && held.getItem() instanceof ItemFood))) {
            double time = System.currentTimeMillis() / 1000D * ClassicBarsConfig.transitionSpeed;
            double foodAlpha = Math.sin(time) / 2 + .5;

            double hungerOverlay;
            double potentialSat;
            if (appleCoreFood) {
                // AppleCore (and Hunger Overhaul through it) may change food values
                hungerOverlay = AppleCoreCompat.getHunger(player, held);
                potentialSat = AppleCoreCompat.getSaturationIncrement(player, held);
            } else {
                ItemFood foodItem = (ItemFood) held.getItem();
                hungerOverlay = foodItem.func_150905_g(held);
                potentialSat = 2 * hungerOverlay * foodItem.func_150906_h(held);
            }

            // Draw potential hunger
            double hungerWidth = Math.min(maxHunger - hunger, hungerOverlay);
            // don't render the bar at all if hunger is full
            if (hunger < maxHunger) {
                double w = ModUtils.getWidth(hungerWidth + hunger, maxHunger);
                f = xStart + (rightHandSide() ? WIDTH - w : 0);
                hungerColor.color2Gla((float) foodAlpha);
                renderPartialBar(f + 2, yStart + 2, w);
            }

            // Draw potential saturation
            if (ClassicBarsConfig.showSaturationBar) {
                // maximum potential saturation cannot combine with current saturation to go over 20
                double saturationWidth = Math.min(potentialSat, maxSat - currentSat);

                // Potential Saturation cannot go over potential hunger + current hunger combined
                saturationWidth = Math.min(saturationWidth, hunger + hungerWidth);
                saturationWidth = Math.min(saturationWidth, hungerOverlay + hunger);
                if ((potentialSat + currentSat) > (hunger + hungerWidth)) {
                    double diff = (potentialSat + currentSat) - (hunger + hungerWidth);
                    saturationWidth = potentialSat - diff;
                }

                double w = ModUtils.getWidth(saturationWidth + currentSat, maxSat);

                f = xStart + (rightHandSide() ? WIDTH - w : 0);
                satColor.color2Gla((float) foodAlpha);
                renderPartialBar(f + 2, yStart + 2, w);
            }
        }

        if (ClassicBarsConfig.showExhaustionOverlay && ClientData.isExhaustionSynced()) {
            // AppleCore can raise the maximum exhaustion level
            double maxExhaustion = AppleCoreCompat.available() ? AppleCoreCompat.getMaxExhaustion(player) : 4D;
            if (maxExhaustion <= 0D) maxExhaustion = 4D;
            exhaustion = (float) Math.min(exhaustion, maxExhaustion);
            f = xStart + (rightHandSide() ? WIDTH - ModUtils.getWidth(exhaustion, maxExhaustion) : 0);
            // draw exhaustion
            GL11.glColor4f(1, 1, 1, .25f);
            ModUtils.drawTexturedModalRect(f + 2, yStart + 1, 1, 28, ModUtils.getWidth(exhaustion, maxExhaustion), 9);
        }
        GL11.glColor4f(1, 1, 1, 1);
    }

    @Override
    public double getBarWidth(EntityPlayer player) {
        double hunger = player.getFoodStats().getFoodLevel();
        return Math.ceil(WIDTH * hunger / 20D);
    }

    public int getSatBarWidth(EntityPlayer player) {
        double saturation = ClientData.getSaturation(player);
        return (int) Math.ceil(WIDTH * saturation / 20D);
    }

    // saturation
    @Override
    public Color getPrimaryBarColor(int index, EntityPlayer player) {
        boolean hunger = player.isPotionActive(Potion.hunger);
        return hunger ? ConfigCache.saturationDebuff : ConfigCache.saturation;
    }

    // hunger
    @Override
    public Color getSecondaryBarColor(int index, EntityPlayer player) {
        boolean hunger = player.isPotionActive(Potion.hunger);
        return hunger ? ConfigCache.hungerDebuff : ConfigCache.hunger;
    }

    @Override
    public boolean shouldRenderText() {
        return ClassicBarsConfig.showHungerNumbers;
    }

    @Override
    public void renderText(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        int xStart = resolution.getScaledWidth() / 2 + getIconOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        double hunger = player.getFoodStats().getFoodLevel();
        int c = getSecondaryBarColor(0, player).colorToText();
        textHelper(xStart, yStart, hunger, c);
    }

    @Override
    public void renderIcon(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        int xStart = resolution.getScaledWidth() / 2 + getIconOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        boolean hungerActive = player.isPotionActive(Potion.hunger);

        Color.reset();
        ModUtils.drawTexturedModalRect(xStart, yStart, 16, 27, 9, 9);
        ModUtils.drawTexturedModalRect(xStart, yStart, hungerActive ? 88 : 52, 27, 9, 9);
    }
}
