package live.ghostly.hcfactions.crate.command.subcommand;


import live.ghostly.hcfactions.crate.Crate;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class CrateListCommand extends PluginCommand {
    @Command(name = "crate.list", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();

        if (!sender.hasPermission("hcf.command." + command.getCommand().getName())) {
            sender.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Listing all registered crates:");
        for (Crate crate : Crate.getCrates()) {
            sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.GRAY + crate.getName());
        }

    }
}
