package live.ghostly.hcfactions.factions.commands.coleader;

import live.ghostly.hcfactions.factions.commands.FactionCommand;
import live.ghostly.hcfactions.factions.type.PlayerFaction;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import live.ghostly.hcfactions.util.player.SimpleOfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionPromoteCommand extends FactionCommand {
    @Command(name = "f.promote", aliases = {"faction.promote", "factions.promote", "f.mod", "factions.mod", "faction.mod", "f.officer", "factions.officer", "faction.officer", "faction.captain", "f.captain", "faction.captain"}, inFactionOnly = true, isCoLeaderOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (command.getArgs().length == 0) {
            player.sendMessage(langConfig.getString("TOO_FEW_ARGS.PROMOTE"));
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        PlayerFaction playerFaction = profile.getFaction();


        UUID uuid;
        String name;
        Player toPromote = Bukkit.getPlayer(command.getArgs(0));

        if (toPromote == null) {
            SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByName(command.getArgs(0));
            if (offlinePlayer != null) {
                uuid = offlinePlayer.getUuid();
                name = offlinePlayer.getName();
            } else {
                player.sendMessage(langConfig.getString("ERROR.NOT_ONLINE").replace("%PLAYER%", command.getArgs(0)));
                return;
            }
        } else {
            uuid = toPromote.getUniqueId();
            name = toPromote.getName();
        }

        if (name.equalsIgnoreCase(player.getName()) && player.getUniqueId().equals(playerFaction.getLeader())) {
            player.sendMessage(langConfig.getString("ERROR.PROMOTE_YOURSELF"));
            return;
        }

        if (!playerFaction.getAllPlayerUuids().contains(uuid)) {
            player.sendMessage(langConfig.getString("ERROR.NOT_IN_YOUR_FACTION").replace("%PLAYER%", name));
            return;
        }

        if (playerFaction.getOfficers().contains(uuid)) {
            if (!playerFaction.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(langConfig.getString("ERROR.NOT_LEADER"));
                return;
            }
        }

        if (playerFaction.getCoLeaders().contains(uuid)) {
            player.sendMessage(langConfig.getString("ERROR.ALREADY_COLEADER").replace("%PLAYER%", name));
            return;
        }

        playerFaction.getMembers().remove(uuid);
        if (playerFaction.getOfficers().contains(uuid)) {
            playerFaction.getOfficers().remove(uuid);
            playerFaction.getCoLeaders().add(uuid);
        } else {
            playerFaction.getOfficers().add(uuid);
        }

        playerFaction.sendMessage(langConfig.getString("ANNOUNCEMENTS.FACTION.PLAYER_PROMOTED").replace("%PLAYER%", name).replace("%LEADER%", player.getName()));
    }
}
