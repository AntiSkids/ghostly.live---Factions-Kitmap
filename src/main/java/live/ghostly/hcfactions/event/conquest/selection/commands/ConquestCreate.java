package live.ghostly.hcfactions.event.conquest.selection.commands;

import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.event.conquest.ConquestEvent;
import live.ghostly.hcfactions.event.conquest.selection.ConquestSelection;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class ConquestCreate extends PluginCommand {
    @Command(name = "conquest.create", aliases = {"conquest.new", "createconquest", "newconquest"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        String[] args = command.getArgs();
        if (!player.hasPermission("hcf.command." + command.getCommand().getName())) {
            player.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Use /conquest create (name).");
            return;
        }

        if (EventManager.getInstance().getByName(StringUtils.join(args)) != null) {
            player.sendMessage(" ");
            player.sendMessage(ChatColor.RED + "An event with that name already exists.");
            player.sendMessage(" ");
            return;
        }

        ConquestSelection conquestSelection = ConquestSelection.getByPlayer(player);

        if (conquestSelection == null) {
            player.sendMessage(" ");
            player.sendMessage(ChatColor.RED + "No conquest zone defined.");
            player.sendMessage(" ");
            return;
        }


        if (conquestSelection.getConquestZones().size() < 5) {
            player.sendMessage(" ");
            player.sendMessage(ChatColor.RED + "No all conquest zone defined.");
            player.sendMessage(" ");
            return;
        }

        ConquestEvent conquestEvent = new ConquestEvent("Conquest", conquestSelection.getConquestZones());
    }
}
