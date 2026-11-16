package tfar.classicbar.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tfar.classicbar.ClassicBar;

@SideOnly(Side.CLIENT)
public class GuiClassicBarConfig extends GuiConfig {

    public GuiClassicBarConfig(GuiScreen parent) {
        super(
                parent,
                getConfigElements(),
                ClassicBar.MODID,
                ClassicBar.MODID,
                false,
                false,
                I18n.format("classicbar.config.title"));
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> elements = new ArrayList<>();
        Configuration config = ClassicBarsConfig.config;
        if (config == null) return elements;
        elements.add(new ConfigElement(config.getCategory(ClassicBarsConfig.CATEGORY_GENERAL)));
        elements.add(new ConfigElement(config.getCategory(ClassicBarsConfig.CATEGORY_COLORS)));
        elements.add(new ConfigElement(config.getCategory(ClassicBarsConfig.CATEGORY_ORDER)));
        return elements;
    }
}
