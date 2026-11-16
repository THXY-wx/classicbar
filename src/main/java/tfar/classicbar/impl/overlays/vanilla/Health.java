package tfar.classicbar.impl.overlays.vanilla;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ColorUtils;
import tfar.classicbar.util.HealthEffect;
import tfar.classicbar.util.ModUtils;

public class Health extends BarOverlayImpl {

    /** Vertical shading of the bar fill (top to bottom), taken from the bar texture. */
    private static final float[] LAYER_SHADE = { 0xE6 / 255F, 0xFF / 255F, 0xE6 / 255F, 0xCD / 255F, 0xB4 / 255F };

    private double playerHealth = 0;
    private double lastPlayerHealth = 0;

    public Health() {
        super("health");
    }

    @Override
    public void renderBar(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        double health = player.getHealth();
        double barWidth = getBarWidth(player);
        boolean highlight = player.hurtResistantTime / 3 % 2 == 1;
        if (player.hurtResistantTime < 10) highlight = false;

        if (health < playerHealth) {
            lastPlayerHealth = playerHealth;
        }
        playerHealth = health;

        double displayHealth = health;
        if (player.hurtTime > 0 && player.maxHurtTime > 0) {
            displayHealth = health + (lastPlayerHealth - health) * (player.hurtTime / (double) player.maxHurtTime);
        }

        int xStart = resolution.getScaledWidth() / 2 + getHOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        double maxHealth = player.getMaxHealth();

        HealthEffect effect = getHealthEffect(player);

        int i4 = highlight ? 18 : 0;

        boolean overlap = useLayers(maxHealth) && isOverlapMode();
        double fillWidth = overlap ? overlapWidth(health) : barWidth;
        double f = xStart + (rightHandSide() ? WIDTH - fillWidth : 0);

        Color.reset();
        // Bar background
        ModUtils.drawTexturedModalRect(xStart, yStart, 0, i4, WIDTH + 4, 9);

        // Pass 1: interpolate the bar while the player is taking damage
        if (!overlap && displayHealth != health && displayHealth > health) {
            Color.reset();
            double w = ModUtils.getWidth(displayHealth, maxHealth);
            double off = rightHandSide() ? w - barWidth : 0;
            renderPartialBar(f + 2 - off, yStart + 2, w);
        }

        // draw portion of bar based on health remaining
        if (overlap) {
            renderOverlap(xStart, yStart, health);
        } else if (useLayers(maxHealth)) {
            renderVectorLayers(xStart, yStart, health, maxHealth, resolution.getScaleFactor());
        } else {
            Color primary = getPrimaryBarColor(0, player);
            primary.color2Gl();
            renderPartialBar(f + 2, yStart + 2, barWidth);
        }

        if (effect == HealthEffect.POISON) {
            GL11.glColor4f(0, .5f, 0, .5f);
            ModUtils.drawTexturedModalRect(f + 1, yStart + 1, 1, 36, fillWidth, 7);
            Color.reset();
        }
    }

    @Override
    public Color getPrimaryBarColor(int index, EntityPlayer player) {
        double health = player.getHealth();
        double maxHealth = player.getMaxHealth();
        HealthEffect effect = getHealthEffect(player);
        if (effect != HealthEffect.NONE) {
            return ColorUtils.calculateScaledColor(health, maxHealth, effect);
        }
        if (useLayers(maxHealth)) {
            return layerColor(currentLayer(health));
        }
        return ColorUtils.calculateScaledColor(health, maxHealth, effect);
    }

    /**
     * Health above 20 can be drawn in three styles, see
     * {@link ClassicBarsConfig#healthLayerMode}.
     */
    private boolean useLayers(double maxHealth) {
        return maxHealth > 20D && !ConfigCache.healthLayers.isEmpty() && !"single".equals(layerMode());
    }

    private String layerMode() {
        String mode = ClassicBarsConfig.healthLayerMode;
        return mode == null ? "layered" : mode.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private boolean isOverlapMode() {
        return "overlap".equals(layerMode());
    }

    /** Index of the 20 HP layer the given health value is in. */
    private int currentLayer(double health) {
        int layer = (int) Math.ceil(health / 20.0D) - 1;
        if (layer < 0) layer = 0;
        if (layer >= ConfigCache.healthLayers.size()) layer = ConfigCache.healthLayers.size() - 1;
        return layer;
    }

    /** Width of the base 20 HP row, used by the overlap style. */
    private double overlapWidth(double health) {
        return Math.min(20.0D, Math.max(0.0D, health)) / 20.0D * WIDTH;
    }

    /**
     * Layered style, drawn as vector geometry (no texture): the fill is made of one
     * pixel wide quads whose left and right vertices carry the blended layer color
     * and whose rows carry the vertical shading. The GPU interpolates the colors
     * across every quad, so the layer boundaries fade smoothly into each other.
     */
    private void renderVectorLayers(int xStart, int yStart, double health, double maxHealth, int scale) {
        double fill = health / maxHealth * WIDTH;
        if (fill <= 0D) return;

        // one quad column per screen pixel, so the gradient is sampled at screen resolution
        int columns = (int) Math.ceil(fill * scale);
        double columnWidth = 1D / scale;
        double stepHp = maxHealth / (WIDTH * (double) scale);
        boolean rightSide = rightHandSide();

        boolean textureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        for (int column = 0; column < columns; column++) {
            double startHp = Math.min(health, column * stepHp);
            double endHp = Math.min(health, (column + 1) * stepHp);
            if (endHp <= startHp) break;

            double width = Math.min(columnWidth, fill - column * columnWidth);
            if (width <= 0.0001D) break;

            Color start = colorAt(startHp, maxHealth);
            Color end = colorAt(endHp, maxHealth);
            double xLeft = rightSide
                    ? xStart + 2 + WIDTH - (column + 1) * columnWidth
                    : xStart + 2 + column * columnWidth;

            addVectorColumn(
                    tessellator,
                    xLeft,
                    yStart,
                    width,
                    rightSide ? end : start,
                    rightSide ? start : end);
        }

        tessellator.draw();

        if (textureEnabled) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        Color.reset();
    }

    private void addVectorColumn(Tessellator tessellator, double xLeft, int yStart, double width, Color left,
            Color right) {
        double xRight = xLeft + width;

        for (int band = 0; band < HEIGHT; band++) {
            float shade = LAYER_SHADE[band];
            double yTop = yStart + 2 + band;
            double yBottom = yTop + 1;

            applyColor(tessellator, left, shade);
            tessellator.addVertex(xLeft, yBottom, 0);
            tessellator.addVertex(xRight, yBottom, 0);
            applyColor(tessellator, right, shade);
            tessellator.addVertex(xRight, yTop, 0);
            tessellator.addVertex(xLeft, yTop, 0);
        }
    }

    private static void applyColor(Tessellator tessellator, Color color, float shade) {
        tessellator.setColorRGBA_F(
                color.r / 255F * shade,
                color.g / 255F * shade,
                color.b / 255F * shade,
                1F);
    }

    /** Color of the bar at the given health value, blended around layer boundaries. */
    private Color colorAt(double hp, double maxHealth) {
        int layer = (int) Math.floor(hp / 20.0D);
        Color current = layerColor(layer);

        double gradient = ClassicBarsConfig.healthLayerGradient;
        if (gradient <= 0D) return current;

        double half = gradient / 2D;
        double boundary = layer * 20.0D;

        // fade in from the previous layer (50/50 on the boundary)
        if (layer > 0 && hp - boundary < half) {
            double f = 0.5D + 0.5D * (hp - boundary) / half;
            return blendHsv(layerColor(layer - 1), current, smoothstep(f));
        }

        // fade out towards the next layer (50/50 on the boundary)
        double nextBoundary = boundary + 20.0D;
        if (nextBoundary < maxHealth && nextBoundary - hp < half) {
            double f = 0.5D * (hp - (nextBoundary - half)) / half;
            return blendHsv(current, layerColor(layer + 1), smoothstep(f));
        }

        return current;
    }

    /** Smoothstep easing so the blend accelerates/decelerates instead of looking linear. */
    private static double smoothstep(double t) {
        double x = Math.max(0D, Math.min(1D, t));
        return x * x * (3D - 2D * x);
    }

    /** HSV blend so transitions keep their hue and vividness instead of turning muddy. */
    private static Color blendHsv(Color from, Color to, double t) {
        double clamped = Math.max(0D, Math.min(1D, t));
        float[] a = rgbToHsv(from.r, from.g, from.b);
        float[] b = rgbToHsv(to.r, to.g, to.b);

        float dh = b[0] - a[0];
        if (dh > 0.5F) dh -= 1F;
        else if (dh < -0.5F) dh += 1F;

        float h = a[0] + dh * (float) clamped;
        if (h < 0F) h += 1F;
        if (h >= 1F) h -= 1F;
        float s = a[1] + (b[1] - a[1]) * (float) clamped;
        float v = a[2] + (b[2] - a[2]) * (float) clamped;

        return hsvToColor(h, s, v);
    }

    private static float[] rgbToHsv(int r, int g, int b) {
        float rf = r / 255F;
        float gf = g / 255F;
        float bf = b / 255F;
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;

        float h = 0F;
        if (delta > 1.0E-6F) {
            if (max == rf) h = (gf - bf) / delta;
            else if (max == gf) h = 2F + (bf - rf) / delta;
            else h = 4F + (rf - gf) / delta;
            h /= 6F;
            if (h < 0F) h += 1F;
        }

        float s = max <= 1.0E-6F ? 0F : delta / max;
        return new float[] { h, s, max };
    }

    private static Color hsvToColor(float h, float s, float v) {
        float hh = (h - (float) Math.floor(h)) * 6F;
        int i = (int) Math.floor(hh);
        float f = hh - i;
        float p = v * (1F - s);
        float q = v * (1F - s * f);
        float t = v * (1F - s * (1F - f));

        float r;
        float g;
        float b;
        switch (i % 6) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            default:
                r = v;
                g = p;
                b = q;
                break;
        }

        return Color.from(Math.round(r * 255F), Math.round(g * 255F), Math.round(b * 255F));
    }

    /**
     * Overlap style: all layers start at the same end of the bar and every higher
     * layer is painted over the previous ones, exactly like Tinkers' colored
     * hearts (the highest layer ends up on the outside).
     */
    private void renderOverlap(int xStart, int yStart, double health) {
        int layers = (int) Math.ceil(health / 20.0D);
        for (int i = 0; i < layers; i++) {
            double widthHp = Math.min(20.0D, health - 20.0D * i);
            if (widthHp <= 0D) continue;
            double widthPx = widthHp / 20.0D * WIDTH;
            layerColor(i).color2Gl();
            double x = rightHandSide() ? xStart + 2 + WIDTH - widthPx : xStart + 2;
            ModUtils.drawTexturedModalRect(
                    x,
                    yStart + 2,
                    (int) (BAR_U + (rightHandSide() ? WIDTH - widthPx : 0)),
                    BAR_V,
                    widthPx,
                    HEIGHT);
        }
        Color.reset();
    }

    private Color layerColor(int layer) {
        if (ConfigCache.healthLayers.isEmpty()) return Color.BLACK;
        // colors repeat for very high max health
        return ConfigCache.healthLayers.get(Math.floorMod(layer, ConfigCache.healthLayers.size()));
    }

    @Override
    public double getBarWidth(EntityPlayer player) {
        double health = player.getHealth();
        double maxHealth = player.getMaxHealth();
        return ModUtils.getWidth(health, maxHealth);
    }

    @Override
    public boolean shouldRenderText() {
        return ClassicBarsConfig.showHealthNumbers;
    }

    @Override
    public void renderText(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        int xStart = resolution.getScaledWidth() / 2 + getIconOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        textHelper(xStart, yStart, player.getHealth(), getPrimaryBarColor(0, player).colorToText());
    }

    @Override
    public void renderIcon(ScaledResolution resolution, EntityPlayer player, int vOffset) {
        HealthEffect effect = getHealthEffect(player);

        int xStart = resolution.getScaledWidth() / 2 + getIconOffset();
        int yStart = resolution.getScaledHeight() - vOffset;
        int i5 = (player.worldObj.getWorldInfo().isHardcoreModeEnabled()) ? 5 : 0;
        // heart background
        Color.reset();
        ModUtils.drawTexturedModalRect(xStart, yStart, 16, 9 * i5, 9, 9);
        // heart
        ModUtils.drawTexturedModalRect(xStart, yStart, 36 + effect.i, 9 * i5, 9, 9);
    }
}
