package tfar.classicbar.client.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tfar.classicbar.EventHandler;
import tfar.classicbar.config.ClassicBarsConfig;

@SideOnly(Side.CLIENT)
public class CommandClassicBar extends CommandBase {

    @Override
    public String getCommandName() {
        return "classicbar";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/classicbar reload";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 1 && "reload".equalsIgnoreCase(args[0])) {
            ClassicBarsConfig.reloadFromDisk();
            EventHandler.cacheConfigs();
            sender.addChatMessage(new ChatComponentText("\u00a7aClassicBar configuration reloaded."));
        } else {
            sender.addChatMessage(new ChatComponentText("\u00a7eUsage: /classicbar reload"));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
