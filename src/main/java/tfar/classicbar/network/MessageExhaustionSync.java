package tfar.classicbar.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import tfar.classicbar.ClassicBar;

/**
 * Sent from the server every time the player's exhaustion level changes,
 * vanilla 1.7.10 never syncs this value at all.
 */
public class MessageExhaustionSync implements IMessage {

    private float exhaustion;

    public MessageExhaustionSync() {}

    public MessageExhaustionSync(float exhaustion) {
        this.exhaustion = exhaustion;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        exhaustion = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(exhaustion);
    }

    public static class Handler implements IMessageHandler<MessageExhaustionSync, IMessage> {

        @Override
        public IMessage onMessage(MessageExhaustionSync message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                ClassicBar.proxy.handleExhaustion(message.exhaustion);
            }
            return null;
        }
    }
}
