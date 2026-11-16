package tfar.classicbar.network;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import tfar.classicbar.ClassicBar;

/**
 * Sent from the server every time the player's saturation level changes, since
 * vanilla 1.7.10 only syncs it when it reaches zero.
 */
public class MessageSaturationSync implements IMessage {

    private float saturation;

    public MessageSaturationSync() {}

    public MessageSaturationSync(float saturation) {
        this.saturation = saturation;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        saturation = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(saturation);
    }

    public static class Handler implements IMessageHandler<MessageSaturationSync, IMessage> {

        @Override
        public IMessage onMessage(MessageSaturationSync message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                ClassicBar.proxy.handleSaturation(message.saturation);
            }
            return null;
        }
    }
}
