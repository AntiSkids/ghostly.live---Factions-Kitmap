package live.ghostly.hcfactions.factions.commands;


import live.ghostly.hcfactions.factions.Faction;
import live.ghostly.hcfactions.factions.claims.Claim;
import live.ghostly.hcfactions.factions.events.player.PlayerInitiateFactionTeleportEvent;
import live.ghostly.hcfactions.factions.type.PlayerFaction;
import live.ghostly.hcfactions.factions.type.SystemFaction;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.profile.cooldown.ProfileCooldownType;
import live.ghostly.hcfactions.profile.teleport.ProfileTeleportTask;
import live.ghostly.hcfactions.profile.teleport.ProfileTeleportType;
import live.ghostly.hcfactions.util.LocationSerialization;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionHomeCommand extends FactionCommand {
    @Command(name = "f.home", aliases = {"faction.home", "factions.home", "f.hq", "faction.hq", "factions.hq"}, isAsync = false)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Profile profile = Profile.getByPlayer(player);

        if (profile.getTeleportWarmup() != null) {
            return;
        }

        if (profile.getCooldownByType(ProfileCooldownType.SPAWN_TAG) != null) {
            player.sendMessage(ChatColor.RED + "You can't do this in spawn tag");
            return;
        }

        Faction faction;
        if (command.getArgs().length >= 1 && player.hasPermission("hcf.bypass")) {
            String name = command.getArgs(0);
            Faction faction1 = PlayerFaction.getAnyByString(name);
            if (faction1 != null) {
                faction = faction1;
            } else {
                player.sendMessage(langConfig.getString("ERROR.NO_FACTIONS_FOUND").replace("%NAME%", name));
                return;
            }
        } else {
            faction = profile.getFaction();

            if (faction == null) {
                player.sendMessage(langConfig.getString("ERROR.NOT_IN_FACTION"));
                return;
            }

        }

        if (faction.getHome() == null) {
            player.sendMessage(langConfig.getString("ERROR.HOME_NOT_SET"));
            return;
        }

        Location location = LocationSerialization.deserializeLocation(faction.getHome());

        Claim claim = Claim.getProminentClaimAt(player.getLocation());

        if (claim != null && claim.getFaction() instanceof SystemFaction && !((SystemFaction) claim.getFaction()).isDeathban()) {
            player.teleport(LocationSerialization.deserializeLocation(faction.getHome()));
            return;
        }

        if (player.hasPermission("hcf.bypass") && command.getArgs().length >= 1) {
            player.teleport(LocationSerialization.deserializeLocation(faction.getHome()));
        } else {

            if (claim != null && (claim.getFaction() instanceof PlayerFaction && (claim.getFaction() != faction) && mainConfig.getBoolean("FACTION_GENERAL.FACTION_HOME_IN_ENEMY_TERRITORY"))) {
                player.sendMessage(langConfig.getString("ERROR.NO_HOME_ENEMY"));
                return;
            }

            if (profile.getProtection() != null) {
                player.sendMessage(langConfig.getString("ERROR.NO_HOME_PVP_PROTECTION"));
                return;
            }

            int time = 0;
            String worldName = player.getLocation().getWorld().getName();
            String root = "TELEPORT_COUNTDOWN.HOME";

            for (String world : new String[]{"OVERWORLD", "NETHER", "END"}) {
                if (worldName.equalsIgnoreCase(mainConfig.getString(world))) {
                    if (!mainConfig.getBoolean(root + "." + world + ".ENABLED")) {
                        player.sendMessage(langConfig.getString("ERROR.NO_HOME_TELEPORT_IN_WORLD"));
                        return;
                    } else {
                        time = mainConfig.getInt(root + "." + world + ".TIME");
                    }
                }
            }

            long hours = TimeUnit.SECONDS.toHours(time);
            long minutes = TimeUnit.SECONDS.toMinutes(time) - (hours * 60);
            long seconds = TimeUnit.SECONDS.toSeconds(time) - ((hours * 60 * 60) + (minutes * 60));

            String formatted;

            if (hours == 0 && minutes > 0 && seconds > 0) {
                formatted = minutes + " minutes and " + seconds + " seconds";
            } else if (hours == 0 && minutes > 0 && seconds == 0) {
                formatted = minutes + " minutes";
            } else if (hours == 0 && minutes == 0 && seconds > 0) {
                formatted = seconds + " seconds";
            } else if (hours > 0 && minutes > 0 && seconds == 0) {
                formatted = hours + " hours and " + minutes + " minutes";
            } else if (hours > 0 && minutes == 0 && seconds > 0) {
                formatted = hours + " hours and " + seconds + " seconds";
            } else {
                formatted = hours + "hours, " + minutes + " minutes and " + seconds + " seconds";
            }

            if (hours == 1) {
                formatted = formatted.replace("hours", "hour");
            }

            if (minutes == 1) {
                formatted = formatted.replace("minutes", "minute");
            }

            if (seconds == 1) {
                formatted = formatted.replace("seconds", "second");
            }


            player.sendMessage(langConfig.getString("FACTION_OTHER.TELEPORTING_TO_HOME").replace("%TIME%", formatted + ""));
            profile.setTeleportWarmup(new ProfileTeleportTask(new PlayerInitiateFactionTeleportEvent(player, profile.getFaction(), ProfileTeleportType.HOME_TELEPORT, time, location, player.getLocation())));
            profile.getTeleportWarmup().runTaskLaterAsynchronously(main, (long) (time * 20));
        }
    }
}
