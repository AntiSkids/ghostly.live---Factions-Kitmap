package live.ghostly.hcfactions.event.sumo.command;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.event.sumo.SumoEvent;
import live.ghostly.hcfactions.factions.Faction;
import live.ghostly.hcfactions.factions.claims.Claim;
import live.ghostly.hcfactions.factions.type.SystemFaction;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.Style;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class JoinSumoCommand extends PluginCommand {
    @Command(name = "join.sumo", isAsync = false)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

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

        if (!sumoEvent.isPreStart()) {
            player.sendMessage(Style.translate("&cSumo event not started."));
            return;
        }

        if (sumoEvent.getPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Style.translate("&cReady in sumo event."));
            return;
        }

        sumoEvent.join(player);

    }
}
