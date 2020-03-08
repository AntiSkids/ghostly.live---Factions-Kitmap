package live.ghostly.hcfactions.event.conquest.selection.commands;

import live.ghostly.hcfactions.event.conquest.ConquestZone;
import live.ghostly.hcfactions.event.conquest.selection.ConquestSelection;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class ConquestZoneClaim extends PluginCommand {

    @Command(name = "conquest.claim")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        String[] args = command.getArgs();
        if (!player.hasPermission("hcf.command." + command.getCommand().getName())) {
            player.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Use /conquest claim (color).");
            return;
        }
        String color = args[0];

        ConquestSelection conquestSelection = ConquestSelection.getByPlayer(player);

        if (conquestSelection == null) {
            conquestSelection = new ConquestSelection(player);
        }

        if (ConquestZone.Color.getByName(color) == null) {
            player.sendMessage(ChatColor.RED + "No Color found.");
            return;
        }

        conquestSelection.setColor(ConquestZone.Color.getByName(color));

        player.sendMessage(" ");
        player.sendMessage(ChatColor.YELLOW + "You have received the zone wand.");
        player.sendMessage(" ");
        player.getInventory().removeItem(ConquestSelection.getWand());
        player.getInventory().addItem(ConquestSelection.getWand());

    }
}
