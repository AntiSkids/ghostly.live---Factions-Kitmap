package live.ghostly.hcfactions.crate.command;

import live.ghostly.hcfactions.crate.command.subcommand.*;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class CrateCommand extends PluginCommand {

    public CrateCommand() {
        new CrateCreateCommand();
        new CrateDeleteCommand();
        new CrateItemsCommand();
        new CrateKeyCommand();
        new CrateListCommand();
    }

    @Command(name = "crate", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender player = command.getSender();
        if (!player.hasPermission("hcf.command." + command.getCommand().getName())) {
            player.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        player.sendMessage(ChatColor.RED + "/create list");
        player.sendMessage(ChatColor.RED + "/crate create <name>");
        player.sendMessage(ChatColor.RED + "/crate delete <name>");
        player.sendMessage(ChatColor.RED + "/crate items <name>");
        player.sendMessage(ChatColor.RED + "/crate key <name> <amount> <player>");
    }
}
