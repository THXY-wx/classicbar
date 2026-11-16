package tfar.classicbar.api;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import tfar.classicbar.util.Color;

/**
 * Public API for custom bars. Register implementations with
 * {@code tfar.classicbar.EventHandler#register(BarOverlay)}.
 */
public interface BarOverlay {

    boolean rightHandSide();

    BarOverlay setSide(boolean right);

    void render(ScaledResolution resolution, EntityPlayer player, int vOffset);

    ResourceLocation getIconRL();

    void bindIconTexture();

    double getBarWidth(EntityPlayer player);

    Color getPrimaryBarColor(int index, EntityPlayer player);

    Color getSecondaryBarColor(int index, EntityPlayer player);

    boolean isFitted();

    String name();
}
