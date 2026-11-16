package tfar.classicbar.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import tfar.classicbar.compat.AppleCoreCompat;
import tfar.classicbar.util.FoodStatsHelper;

/**
 * Pushes saturation and exhaustion from the server to the owning client. Both
 * values are otherwise only known to the server in 1.7.10.
 */
public final class SyncHandler {

    private static SyncHandler instance;

    private final Map<UUID, Float> lastSaturation = new HashMap<>();
    private final Map<UUID, Float> lastExhaustion = new HashMap<>();

    private SyncHandler() {}

    public static SyncHandler instance() {
        if (instance == null) {
            instance = new SyncHandler();
        }
        return instance;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (player.worldObj == null || player.worldObj.isRemote) return;
        sync(player);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        sync((EntityPlayerMP) event.player);
    }

    private void sync(EntityPlayerMP player) {
        // AppleCore already syncs saturation and exhaustion to its clients, so
        // ClassicBar does not need to send its own packets when it is installed.
        if (AppleCoreCompat.available()) return;

        UUID id = player.getUniqueID();

        float saturation = player.getFoodStats().getSaturationLevel();
        Float last = lastSaturation.get(id);
        if (last == null || last.floatValue() != saturation) {
            Message.channel().sendTo(new MessageSaturationSync(saturation), player);
            lastSaturation.put(id, saturation);
        }

        float exhaustion = FoodStatsHelper.getExhaustion(player.getFoodStats());
        Float lastEx = lastExhaustion.get(id);
        if (lastEx == null || Math.abs(lastEx - exhaustion) >= 0.01F) {
            Message.channel().sendTo(new MessageExhaustionSync(exhaustion), player);
            lastExhaustion.put(id, exhaustion);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        EntityPlayer player = event.player;
        if (player == null) return;
        UUID id = player.getUniqueID();
        lastSaturation.remove(id);
        lastExhaustion.remove(id);
    }
}
