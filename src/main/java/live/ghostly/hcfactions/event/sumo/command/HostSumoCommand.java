package live.ghostly.hcfactions.event.sumo.command;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.event.sumo.SumoEvent;
import live.ghostly.hcfactions.factions.Faction;
import live.ghostly.hcfactions.factions.claims.Claim;
import live.ghostly.hcfactions.factions.type.SystemFaction;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.Clickable;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.Style;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HostSumoCommand extends PluginCommand {
    @Command(name = "host.sumo")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (!player.hasPermission("sumo.host")) {
            player.sendMessage(Style.translate("&cNo permissions"));
            return;
        }

        if (!FactionsPlugin.getInstance().isKitmapMode()) {
            player.sendMessage(Style.translate("&cOnly available in kitmap"));
            return;
        }

        Location location = player.getLocation();
        Claim claim = Claim.getProminentClaimAt(location);
        if (claim != null) {
            Profile profile = Profile.getByPlayer(player);
            Faction factionAt = claim.getFaction();

            if (factionAt != null) {
                if (factionAt instanceof SystemFaction) {
                    if (((SystemFaction) factionAt).isDeathban()) {
                        player.sendMessage(ChatColor.RED + "You can only use this command in your faction or in the spawn");
                        return;
                    }
                } else if (!profile.getFaction().getName().equals(factionAt.getName())) {
                    player.sendMessage(ChatColor.RED + "You can only use this command in your faction or in the spawn");
                    return;
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You can only use this command in your faction or in the spawn");
            return;
        }

        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if (sumoEvent.isStarted() || sumoEvent.isPreStart()) {
            player.sendMessage(Style.translate("&cSumo event ready started."));
            return;
        }

        sumoEvent.join(player);
        sumoEvent.preStart(player);

        String toSend = Style.translate("&c&lSumo &fhosted by &c" + player.getName() + "  &fis starting..." +
                " &7(&a" + sumoEvent.getPlayers().size() + "&7/&a100&7) &b!Click to join!");

        Clickable message = new Clickable(toSend,
                Style.RED + "Click to join this event.",
                "/join sumo");
        Bukkit.getServer().getOnlinePlayers().stream().filter(other -> !sumoEvent.getPlayers().containsKey(other.getUniqueId())).forEach(other -> {
            other.sendMessage(" ");
            message.sendToPlayer(other);
            other.sendMessage(" ");
        });
    }
}
