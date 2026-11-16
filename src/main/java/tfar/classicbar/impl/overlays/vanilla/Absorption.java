package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;

import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

public class Absorption extends BarOverlayImpl {

    public Absorption() {
        super("absorption");
    }

    @Override
    public boolean shouldRender(EntityPlayer player) {
        return player.getAbsorptionAmount() > 0;
    }

    @Override
    public void renderBar(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        double absorb = player.getAbsorptionAmount();
        double barWidth = getBarWidth(player);

        int xStart = resolution.getScaledWidth() / 2 + getHOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        double maxHealth = player.getMaxHealth();

        if (rightHandSide()) {
            xStart += WIDTH - barWidth;
        }

        int index = Math.min((int) Math.ceil(absorb / maxHealth), ConfigCache.absorption.size()) - 1;
        if (index < 0) index = 0;
        Color primary = getPrimaryBarColor(index, player);
        Color.reset();
        // draw background bar
        renderBarBackground(player, resolution.getScaledWidth(), resolution.getScaledHeight(), vOffset);
        if (index == 0) {
            // no wrapping
            primary.color2Gl();
            renderPartialBar(xStart + 2, yStart + 2, barWidth);
        } else {
            // we have wrapped, draw 2 bars
            Color secondary = getSecondaryBarColor(index - 1, player);
            secondary.color2Gl();
            renderFullBar(xStart + 2, yStart + 2);
            if (absorb % maxHealth != 0 && index < ConfigCache.absorption.size() - 1) {
                primary.color2Gl();
                renderPartialBar(
                        xStart + 2,
                        yStart + 2,
                        ModUtils.getWidth(absorb % maxHealth, maxHealth));
            }
        }
    }

    @Override
    public double getBarWidth(EntityPlayer player) {
        double absorb = player.getAbsorptionAmount();
        double maxHealth = player.getMaxHealth();
        return Math.ceil(WIDTH * Math.min(maxHealth, absorb) / maxHealth);
    }

    @Override
    public Color getPrimaryBarColor(int index, EntityPlayer player) {
        if (ConfigCache.absorption.isEmpty()) return Color.BLACK;
        if (index >= ConfigCache.absorption.size()) index = ConfigCache.absorption.size() - 1;
        return ConfigCache.absorption.get(index);
    }

    @Override
    public boolean isFitted() {
        return !ClassicBarsConfig.fullAbsorptionBar;
    }

    @Override
    public boolean shouldRenderText() {
        return ClassicBarsConfig.showAbsorptionNumbers;
    }

    @Override
    public void renderText(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        double absorb = player.getAbsorptionAmount();
        double maxHealth = player.getMaxHealth();
        int xStart = resolution.getScaledWidth() / 2 + getIconOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        int index = Math.min((int) Math.ceil(absorb / maxHealth), ConfigCache.absorption.size()) - 1;
        if (index < 0) index = 0;
        Color c = getPrimaryBarColor(index, player);
        textHelper(xStart, yStart, absorb, c.colorToText());
    }

    @Override
    public void renderIcon(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        int xStart = resolution.getScaledWidth() / 2 + getIconOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        int i5 = (player.worldObj.getWorldInfo().isHardcoreModeEnabled()) ? 5 : 0;
        Color.reset();
        ModUtils.drawTexturedModalRect(xStart, yStart, 16, 9 * i5, 9, 9);
        ModUtils.drawTexturedModalRect(xStart, yStart, 160, 9 * i5, 9, 9);
    }
}
