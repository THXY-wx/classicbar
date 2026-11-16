package tfar.classicbar;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.network.Message;
import tfar.classicbar.network.SyncHandler;

@Mod(
        modid = ClassicBar.MODID,
        name = ClassicBar.NAME,
        version = Tags.VERSION,
        acceptedMinecraftVersions = "[1.7.10]",
        acceptableRemoteVersions = "*",
        guiFactory = "tfar.classicbar.config.ClassicBarGuiFactory")
public class ClassicBar {

    public static final String MODID = "classicbar";
    public static final String NAME = "Classic Bar";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static ClassicBar instance;

    @SidedProxy(clientSide = "tfar.classicbar.ClientProxy", serverSide = "tfar.classicbar.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ClassicBarsConfig.init(event.getSuggestedConfigurationFile());
        Message.registerMessages();
        MinecraftForge.EVENT_BUS.register(SyncHandler.instance());
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
