package tfar.classicbar.impl;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tfar.classicbar.EventHandler;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

@SideOnly(Side.CLIENT)
public abstract class BarOverlayImpl implements BarOverlay {

    /** maximum width the bar can be */
    public static final int WIDTH = 77;
    public static final int HEIGHT = 5;
    public static final int BAR_U = 2;
    public static final int BAR_V = 11;
    public static final ResourceLocation ICON_BAR = new ResourceLocation("classicbar", "textures/gui/health.png");

    protected final String name;
    protected boolean side;

    public BarOverlayImpl(String name) {
        this.name = name;
    }

    public boolean shouldRender(EntityPlayer player) {
        return true;
    }

    @Override
    public final boolean rightHandSide() {
        return side;
    }

    @Override
    public final BarOverlay setSide(boolean right) {
        side = right;
        return this;
    }

    @Override
    public void render(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        if (!shouldRender(player)) return;

        ModUtils.mc().getTextureManager().bindTexture(ICON_BAR);
        renderBar(resolution, player, vOffset);
        if (shouldRenderText()) {
            renderText(resolution, player, vOffset);
        }
        if (ConfigCache.icons) {
            ModUtils.mc().getTextureManager().bindTexture(getIconRL());
            renderIcon(resolution, player, vOffset);
        }
        EventHandler.increment(rightHandSide(), 10);
    }

    public abstract void renderBar(ScaledResolution resolution, EntityPlayer player, int vOffset);

    protected boolean shouldFlash(EntityPlayer player) {
        return false;
    }

    public boolean shouldRenderText() {
        return true;
    }

    public abstract void renderText(ScaledResolution resolution, EntityPlayer player, int vOffset);

    public abstract void renderIcon(ScaledResolution resolution, EntityPlayer player, int vOffset);

    public int getHOffset() {
        return rightHandSide() ? 10 : -91;
    }

    public int getIconOffset() {
        return rightHandSide() ? 92 : -101;
    }

    protected HealthEffect getHealthEffect(EntityPlayer player) {
        HealthEffect effects = HealthEffect.NONE;
        if (player.isPotionActive(net.minecraft.potion.Potion.poison)) effects = HealthEffect.POISON;
        else if (player.isPotionActive(net.minecraft.potion.Potion.wither)) effects = HealthEffect.WITHER;
        return effects;
    }

    public void renderBarBackground(EntityPlayer player, int screenWidth, int screenHeight, int vOffset) {
        double barWidth = getBarWidth(player);
        int xStart = screenWidth / 2 + getHOffset();
        if (isFitted() && rightHandSide()) {
            xStart += WIDTH - barWidth;
        }
        int yStart = screenHeight - vOffset;

        if (isFitted()) {
            drawScaledBarBackground(barWidth, xStart, yStart + 1);
        } else {
            renderFullBarBackground(xStart, yStart);
        }
    }

    public void drawScaledBarBackground(double barWidth, int x, int y) {
        if (rightHandSide()) {
            ModUtils.drawTexturedModalRect(x, y - 1, 0, 0, barWidth + 2, 9);
            ModUtils.drawTexturedModalRect(x + barWidth + 2, y - 1, WIDTH + 2, 0, 2, 9);
        } else {
            ModUtils.drawTexturedModalRect(x, y - 1, 0, 0, (int) (barWidth + 2), 9);
            ModUtils.drawTexturedModalRect((int) (x + barWidth + 2), y - 1, WIDTH + 2, 0, 2, 9);
        }
    }

    public void textHelper(int xStart, int yStart, double stat, int color) {
        int i1 = (int) Math.floor(stat);
        int i2 = ConfigCache.icons ? 1 : 0;

        if (rightHandSide()) {
            ModUtils.drawStringOnHUD(i1 + "", xStart + 9 * i2, yStart - 1, color);
        } else {
            int i3 = ModUtils.getStringLength(i1 + "");
            ModUtils.drawStringOnHUD(i1 + "", xStart - 9 * i2 - i3 + 5, yStart - 1, color);
        }
    }

    public void renderFullBarBackground(int xStart, int yStart) {
        ModUtils.drawTexturedModalRect(xStart, yStart, 0, 0, WIDTH + 4, 9);
    }

    public void renderFullBar(int xStart, int yStart) {
        renderPartialBar(xStart, yStart, WIDTH);
    }

    public void renderPartialBar(double xStart, int yStart, double barWidth) {
        ModUtils.drawTexturedModalRect(xStart, yStart, BAR_U, BAR_V, barWidth, HEIGHT);
    }

    @Override
    public Color getPrimaryBarColor(int index, EntityPlayer player) {
        return Color.BLACK;
    }

    @Override
    public Color getSecondaryBarColor(int index, EntityPlayer player) {
        return Color.BLACK;
    }

    @Override
    public ResourceLocation getIconRL() {
        return new ResourceLocation("textures/gui/icons.png");
    }

    @Override
    public void bindIconTexture() {
        ModUtils.mc().getTextureManager().bindTexture(getIconRL());
    }

    @Override
    public boolean isFitted() {
        return false;
    }

    @Override
    public final String name() {
        return name;
    }
}
