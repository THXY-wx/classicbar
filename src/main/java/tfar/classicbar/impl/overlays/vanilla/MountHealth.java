package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ColorUtils;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

public class MountHealth extends BarOverlayImpl {

    public MountHealth() {
        super("health_mount");
    }

    @Override
    public boolean shouldRender(EntityPlayer player) {
        return player.ridingEntity instanceof EntityLivingBase;
    }

    @Override
    public void renderBar(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        EntityLivingBase mount = (EntityLivingBase) player.ridingEntity;
        if (mount == null || !mount.isEntityAlive()) return;

        double mountHealth = mount.getHealth();
        double maxHealth = mount.getMaxHealth();
        double barWidth = getBarWidth(player);

        boolean highlight = player.hurtTime > 0 && (player.hurtTime / 3) % 2 == 1;

        int xStart = resolution.getScaledWidth() / 2 + getHOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        int i4 = highlight ? 18 : 0;

        Color.reset();
        ModUtils.drawTexturedModalRect(xStart, yStart, 0, i4, WIDTH + 4, 9);

        ColorUtils.calculateScaledColor(mountHealth, maxHealth, HealthEffect.NONE).color2Gl();
        double f = xStart + (rightHandSide() ? WIDTH - barWidth : 0);
        renderPartialBar(f + 2, yStart + 2, barWidth);
    }

    @Override
    public double getBarWidth(EntityPlayer player) {
        if (!(player.ridingEntity instanceof EntityLivingBase)) return 0;
        EntityLivingBase mount = (EntityLivingBase) player.ridingEntity;
        double health = mount.getHealth();
        double maxHealth = mount.getMaxHealth();
        return ModUtils.getWidth(health, maxHealth);
    }

    @Override
    public boolean shouldRenderText() {
        return ClassicBarsConfig.showMountHealthNumbers;
    }

    @Override
    public void renderText(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        if (!(player.ridingEntity instanceof EntityLivingBase)) return;
        EntityLivingBase mount = (EntityLivingBase) player.ridingEntity;
        int xStart = resolution.getScaledWidth() / 2 + getIconOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        double maxHealth = mount.getMaxHealth();
        double health = mount.getHealth();
        textHelper(
                xStart,
                yStart,
                health,
                ColorUtils.calculateScaledColor(health, maxHealth, HealthEffect.NONE).colorToText());
    }

    @Override
    public void renderIcon(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        int xStart = resolution.getScaledWidth() / 2 + getIconOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        Color.reset();
        // heart background (horse heart container)
        ModUtils.drawTexturedModalRect(xStart, yStart, 52, 9, 9, 9);
        // horse heart
        ModUtils.drawTexturedModalRect(xStart, yStart, 88, 9, 9, 9);
    }
}

