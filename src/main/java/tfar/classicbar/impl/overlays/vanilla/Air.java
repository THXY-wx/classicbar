package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;

import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class Air extends BarOverlayImpl {

    private static final int MAX_AIR = 300;

    public Air() {
        super("air");
    }

    @Override
    public boolean shouldRender(EntityPlayer player) {
        return player.getAir() < MAX_AIR;
    }

    @Override
    public void renderBar(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        int xStart = resolution.getScaledWidth() / 2 + getHOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        double barWidth = getBarWidth(player);
        Color.reset();
        // Bar background
        renderFullBarBackground(xStart, yStart);
        // draw portion of bar based on air amount
        double f = xStart + (rightHandSide() ? WIDTH - barWidth : 0);
        Color color = getPrimaryBarColor(0, player);
        color.color2Gl();
        renderPartialBar(f + 2, yStart + 2, barWidth);
    }

    @Override
    public boolean shouldRenderText() {
        return ClassicBarsConfig.showAirNumbers;
    }

    @Override
    public double getBarWidth(EntityPlayer player) {
        int air = player.getAir();
        return Math.ceil((double) WIDTH * air / MAX_AIR);
    }

    @Override
    public Color getPrimaryBarColor(int index, EntityPlayer player) {
        return ConfigCache.air;
    }

    @Override
    public void renderText(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        // draw air amount
        int air = player.getAir();
        int xStart = resolution.getScaledWidth() / 2 + getIconOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        Color color = getPrimaryBarColor(0, player);
        textHelper(xStart, yStart, air / 20, color.colorToText());
    }

    @Override
    public void renderIcon(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        int xStart = resolution.getScaledWidth() / 2 + getIconOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        Color.reset();
        ModUtils.drawTexturedModalRect(xStart, yStart, 16, 18, 9, 9);
    }
}
