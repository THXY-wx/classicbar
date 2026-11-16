package tfar.classicbar.config;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.EventHandler;

/**
 * Applies changes made in the in-game config GUI (mod list -> Config) without
 * requiring a game restart. 1.7.10 fires {@link ConfigChangedEvent} on the FML
 * event bus (not the Forge bus), and the GUI has already written the new values
 * into the in-memory Configuration, so the file must not be re-read here.
 */
@SideOnly(Side.CLIENT)
public class ConfigChangedHandler {

    public void register() {
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (!ClassicBar.MODID.equals(event.modID)) return;
        ClassicBarsConfig.load();
        EventHandler.cacheConfigs();
    }
}
