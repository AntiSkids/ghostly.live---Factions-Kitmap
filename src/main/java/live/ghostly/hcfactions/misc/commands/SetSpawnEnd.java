package live.ghostly.hcfactions.misc.commands;

import live.ghostly.hcfactions.util.LocationSerialization;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnEnd extends PluginCommand {

    @Command(name = "setspawnend", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        if (!(sender.hasPermission("hcf.setspawnend"))) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            this.main.getConfig().set("spawnend", LocationSerialization.serializeLocation(player.getLocation()));
            this.main.saveConfig();
            player.sendMessage(ChatColor.GREEN + "End spawn set.");
        }
    }
}
