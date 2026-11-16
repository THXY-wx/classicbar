package tfar.classicbar.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import tfar.classicbar.ClassicBar;

public final class Message {

    private static SimpleNetworkWrapper channel;

    public static SimpleNetworkWrapper channel() {
        return channel;
    }

    public static void registerMessages() {
        if (channel != null) return;
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(ClassicBar.MODID);
        channel.registerMessage(MessageSaturationSync.Handler.class, MessageSaturationSync.class, 0, Side.CLIENT);
        channel.registerMessage(MessageExhaustionSync.Handler.class, MessageExhaustionSync.class, 1, Side.CLIENT);
    }

    private Message() {}
}
