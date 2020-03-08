package live.ghostly.hcfactions.event.glowstone.command;

import live.ghostly.hcfactions.event.Event;
import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.event.glowstone.GlowstoneEvent;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class GlowstoneForceCommand extends PluginCommand {

    @Command(name = "glowstone.force")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (!sender.hasPermission("hcf.command." + command.getCommand().getName())) {
            sender.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/glowstone force <zone>");
            return;
        }

        Event event = EventManager.getInstance().getByName(args[0]);

        if (event == null || (!(event instanceof GlowstoneEvent))) {
            sender.sendMessage(ChatColor.RED + "Please specify a valid Glowstone Mountain.");
            return;
        }

        GlowstoneEvent glowstoneEvent = (GlowstoneEvent) event;
        glowstoneEvent.start();


    }
}
