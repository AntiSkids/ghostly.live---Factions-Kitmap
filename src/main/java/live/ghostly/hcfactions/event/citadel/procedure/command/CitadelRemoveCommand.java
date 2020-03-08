package live.ghostly.hcfactions.event.citadel.procedure.command;

import live.ghostly.hcfactions.event.Event;
import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.event.citadel.CitadelEvent;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class CitadelRemoveCommand extends PluginCommand {

    @Command(name = "citadel.remove", aliases = {"koth.delete", "removekoth", "kothremove"})
    public void onCommand(CommandArgs command) {


        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (!sender.hasPermission("hcf.command." + command.getCommand().getName())) {
            sender.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/citadel remove <citadel>");
            return;
        }

        Event event = EventManager.getInstance().getByName(args[0]);

        if (event == null || (!(event instanceof CitadelEvent))) {
            sender.sendMessage(ChatColor.RED + "Please specify a valid Citadel.");
            return;
        }


        CitadelEvent koth = (CitadelEvent) event;
        sender.sendMessage(ChatColor.YELLOW + "(" + koth.getName() + ") Citadel has been removed.");
        koth.remove();

    }
}
