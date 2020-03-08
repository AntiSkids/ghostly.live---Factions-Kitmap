package live.ghostly.hcfactions.inventory.command;


import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GiveInventoryCommand extends PluginCommand {

    @Command(name = "giveinv", aliases = {"sendinv", "cpto"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (!player.hasPermission("hcf.command." + command.getCommand().getName())) {
            player.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        Player toSend;
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/" + command.getLabel() + " <player>");
            return;
        } else {
            toSend = Bukkit.getPlayer(StringUtils.join(args));
            if (toSend == null) {
                player.sendMessage(ChatColor.RED + "No player named '" + StringUtils.join(args) + "' found.");
                return;
            }
        }

        toSend.getInventory().setContents(player.getInventory().getContents());
        toSend.getInventory().setArmorContents(player.getInventory().getArmorContents());
        player.sendMessage(ChatColor.RED + "Inventory successfully sent.");
    }
}
