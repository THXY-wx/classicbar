package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class Armor extends BarOverlayImpl {

    public Armor() {
        super("armor");
    }

    @Override
    public boolean shouldRender(EntityPlayer player) {
        return calculateArmorValue(player) >= 1;
    }

    @Override
    public void renderBar(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        double armor = calculateArmorValue(player);
        double barWidth = getBarWidth(player);

        boolean warn = shouldFlash(player);
        float armorAlpha = warn ? (System.currentTimeMillis() / 500) % 2 : 1;
        int xStart = resolution.getScaledWidth() / 2 + getHOffset();

        if (rightHandSide()) {
            xStart += WIDTH - barWidth;
        }

        int yStart = resolution.getScaledHeight() - vOffset;
        // bar background
        renderBarBackground(player, resolution.getScaledWidth(), resolution.getScaledHeight(), vOffset);
        // how many layers are there? remember to start at 0
        int index = (int) Math.min(Math.ceil(armor / 20), ConfigCache.armor.size()) - 1;
        if (index < 0) index = 0;
        Color primary = getPrimaryBarColor(index, player);

        if (index == 0) {
            // calculate bar color
            primary.color2Gla(armorAlpha);
            // draw portion of bar based on armor
            renderPartialBar(xStart + 2, yStart + 2, barWidth);
        } else {
            // we have wrapped, draw 2 bars
            // case 1: bar is not capped and is partially filled
            if (armor % 20 != 0) {
                Color secondary = getSecondaryBarColor(index - 1, player);
                // draw complete first bar
                secondary.color2Gla(armorAlpha);
                renderFullBar(xStart + 2, yStart + 2);
                // draw partial second bar
                primary.color2Gl();
                double w = ModUtils.getWidth(armor % 20, 20);
                double f = xStart + (rightHandSide() ? WIDTH - w : 0);
                renderPartialBar(f + 2, yStart + 2, w);
            }
            // case 2, bar is a multiple of 20, or it is capped
            else {
                // draw complete second bar
                primary.color2Gla(armorAlpha);
                renderFullBar(xStart + 2, yStart + 2);
            }
            // now handle the low armor warning
            if (warn) {
                primary.color2Gla(armorAlpha);
                renderPartialBar(xStart + 2, yStart + 2, ModUtils.getWidth(armor - index * 20, 20));
            }
        }
        GL11.glColor4f(1, 1, 1, 1);
    }

    @Override
    protected boolean shouldFlash(EntityPlayer player) {
        return ClassicBarsConfig.lowArmorWarning && getDamagedAmount(player) > 0;
    }

    @Override
    public double getBarWidth(EntityPlayer player) {
        int armor = calculateArmorValue(player);
        return Math.ceil(WIDTH * Math.min(20, armor) / 20D);
    }

    @Override
    public Color getPrimaryBarColor(int index, EntityPlayer player) {
        if (ConfigCache.armor.isEmpty()) return Color.BLACK;
        if (index >= ConfigCache.armor.size()) index = ConfigCache.armor.size() - 1;
        return ConfigCache.armor.get(index);
    }

    @Override
    public Color getSecondaryBarColor(int index, EntityPlayer player) {
        return getPrimaryBarColor(index, player);
    }

    @Override
    public boolean isFitted() {
        return !ClassicBarsConfig.fullArmorBar;
    }

    @Override
    public boolean shouldRenderText() {
        return ClassicBarsConfig.showArmorNumbers;
    }

    public static int getDamagedAmount(EntityPlayer player) {
        int warningAmount = 0;
        for (int i = 0; i < player.inventory.armorInventory.length; i++) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (!(stack != null && stack.getItem() instanceof ItemArmor)) continue;
            ItemArmor armor = (ItemArmor) stack.getItem();
            int max = stack.getMaxDamage();
            int current = stack.getItemDamage();
            int percentage = 100;
            if (max != 0) percentage = 100 * (max - current) / max;
            if (percentage < 5) {
                warningAmount += armor.damageReduceAmount;
            }
        }
        return warningAmount;
    }

    @Override
    public void renderText(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        int xStart = resolution.getScaledWidth() / 2 + getIconOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        double armor = calculateArmorValue(player);
        int index = (int) Math.min(Math.ceil(armor / 20), ConfigCache.armor.size()) - 1;
        if (index < 0) index = 0;
        int c = getPrimaryBarColor(index, player).colorToText();
        textHelper(xStart, yStart, armor, c);
    }

    @Override
    public void renderIcon(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        int xStart = resolution.getScaledWidth() / 2 + getIconOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        Color.reset();
        ModUtils.drawTexturedModalRect(xStart, yStart, 16, 9, 9, 9);
        ModUtils.drawTexturedModalRect(xStart, yStart, 34, 9, 9, 9);
    }

    private static int calculateArmorValue(EntityPlayer player) {
        return player.getTotalArmorValue();
    }
}
