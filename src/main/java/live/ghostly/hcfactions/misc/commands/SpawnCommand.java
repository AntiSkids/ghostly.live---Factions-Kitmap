package live.ghostly.hcfactions.misc.commands;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.event.sumo.SumoEvent;
import live.ghostly.hcfactions.factions.events.player.PlayerInitiateFactionTeleportEvent;
import live.ghostly.hcfactions.factions.type.SystemFaction;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.profile.cooldown.ProfileCooldownType;
import live.ghostly.hcfactions.profile.teleport.ProfileTeleportTask;
import live.ghostly.hcfactions.profile.teleport.ProfileTeleportType;
import live.ghostly.hcfactions.util.LocationSerialization;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.Style;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class SpawnCommand extends PluginCommand {

    @Command(name = "spawn", inGameOnly = false, isAsync = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if (sumoEvent.getPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't use that command when you're at sumo");
            return;
        }

        if (player.hasPermission("hcf.spawn")) {
            Player toTeleport;
            if (args.length == 0) {
                toTeleport = command.getPlayer();
            } else if (player.hasPermission("hcf.spawn.other")) {
                toTeleport = Bukkit.getPlayer(args[0]);
            } else {
                return;
            }

            if (toTeleport == null) {
                player.sendMessage(ChatColor.RED + "Invalid target.");
                return;
            }

            if (getSpawnLocation() == null) {
                player.sendMessage(ChatColor.RED + "Spawn location not set.");
                return;
            }

            toTeleport.teleport(getSpawnLocation());
            toTeleport.sendMessage(ChatColor.GOLD + "You have successfully teleported to " + ChatColor.YELLOW + "Spawn" + ChatColor.GOLD + ".");

            if (!toTeleport.getName().equalsIgnoreCase(sender.getName())) {
                player.sendMessage(ChatColor.GOLD + "You have successfully teleported " + ChatColor.YELLOW + toTeleport.getName() + ChatColor.GOLD + " to " + ChatColor.YELLOW + "Spawn" + ChatColor.GOLD + ".");
            }
            return;
        }
        Profile profile = Profile.getByPlayer(player);

        if (profile.getTeleportWarmup() != null) {
            return;
        }
        if (profile.getCooldownByType(ProfileCooldownType.SPAWN_TAG) != null) {
            player.sendMessage(ChatColor.RED + "You can't do this in spawn tag");
            return;
        }

        World world = player.getWorld();
        int time = 10;

        if (world.getEnvironment() == World.Environment.NETHER || world.getEnvironment() == World.Environment.THE_END) {
            time = 20;
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

        player.sendMessage(Style.translate("&eYou will be teleported to spawn in " + formatted + "..."));
        profile.setTeleportWarmup(new ProfileTeleportTask(new PlayerInitiateFactionTeleportEvent(player, profile.getFaction(), ProfileTeleportType.SPAWN, time, getSpawnLocation(), player.getLocation())));
        profile.getTeleportWarmup().runTaskLaterAsynchronously(main, (long) (time * 20));
    }

    private Location getSpawnLocation() {
        SystemFaction faction = SystemFaction.getByName("Spawn");
        if (faction != null && faction.getHome() != null) {
            return LocationSerialization.deserializeLocation(faction.getHome());
        }
        return null;
    }
}
