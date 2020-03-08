package live.ghostly.hcfactions.kits.command.subcommand;

import live.ghostly.hcfactions.kits.Kit;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class KitListCommand extends PluginCommand {
    @Command(name = "kit.list", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();

        sender.sendMessage(ChatColor.GREEN + "Listing all registered kits:");
        for (Kit kit : Kit.getKits()) {
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + kit.getName());
        }

    }
}
