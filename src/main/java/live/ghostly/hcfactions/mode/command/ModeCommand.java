package live.ghostly.hcfactions.mode.command;


import live.ghostly.hcfactions.mode.command.subcommand.ModeCreateCommand;
import live.ghostly.hcfactions.mode.command.subcommand.ModeDeleteCommand;
import live.ghostly.hcfactions.mode.command.subcommand.ModeStartCommand;
import live.ghostly.hcfactions.mode.command.subcommand.ModeStopCommand;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class ModeCommand extends PluginCommand {

    public ModeCommand() {
        new ModeCreateCommand();
        new ModeDeleteCommand();
        new ModeStartCommand();
        new ModeStopCommand();
    }

    @Command(name = "mode", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender player = command.getSender();

        if (!player.hasPermission("hcf.command." + command.getCommand().getName())) {
            player.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        player.sendMessage(ChatColor.RED + "/mode create <name>");
        player.sendMessage(ChatColor.RED + "/mode delete <name>");
        player.sendMessage(ChatColor.RED + "/mode start <name>");
        player.sendMessage(ChatColor.RED + "/mode stop <name>");
    }
}
