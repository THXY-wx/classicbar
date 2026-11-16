package tfar.classicbar;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tfar.classicbar.client.ClientConnectionHandler;
import tfar.classicbar.client.ClientData;
import tfar.classicbar.client.command.CommandClassicBar;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.impl.overlays.vanilla.Absorption;
import tfar.classicbar.impl.overlays.vanilla.Air;
import tfar.classicbar.impl.overlays.vanilla.Armor;
import tfar.classicbar.impl.overlays.vanilla.Health;
import tfar.classicbar.impl.overlays.vanilla.Hunger;
import tfar.classicbar.impl.overlays.vanilla.MountHealth;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        new tfar.classicbar.config.ConfigChangedHandler().register();
        FMLCommonHandler.instance().bus().register(new ClientConnectionHandler());

        EventHandler.registerAll(
                new Absorption(),
                new Air(),
                new Armor(),
                new Health(),
                new Hunger(),
                new MountHealth());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        EventHandler.cacheConfigs();
        ClientCommandHandler.instance.registerCommand(new CommandClassicBar());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        // Re-read once every mod has had a chance to register its bars.
        ClassicBarsConfig.load();
        EventHandler.cacheConfigs();
        tfar.classicbar.compat.TConstructCompat.disableTinkersHealthBar();
    }

    @Override
    public void handleSaturation(float saturation) {
        ClientData.setSaturation(saturation);
    }

    @Override
    public void handleExhaustion(float exhaustion) {
        ClientData.setExhaustion(exhaustion);
    }
}




