package live.ghostly.hcfactions.misc.commands;

import live.ghostly.hcfactions.factions.type.PlayerFaction;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TellLocationCommand extends PluginCommand {

    @Command(name = "telllocation", inGameOnly = true, aliases = "tl")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Profile profile = Profile.getByPlayer(player);

        if (profile == null) {
            return;
        }

        PlayerFaction faction = profile.getFaction();

        if (faction == null) {
            player.sendMessage(this.main.getLanguageConfig().getString("ERROR.NOT_IN_FACTION"));
            return;
        }

        faction.sendMessage(this.main.getLanguageConfig().getString("ANNOUNCEMENTS.FACTION.PLAYER_FACTION_CHAT").replace("%PLAYER%", player.getName()).replace("%MESSAGE%", ChatColor.YELLOW + this.getCords(player)).replace("%FACTION%", faction.getName()));
    }

    private String getCords(Player player) {
        return "[" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + "]";
    }
}
