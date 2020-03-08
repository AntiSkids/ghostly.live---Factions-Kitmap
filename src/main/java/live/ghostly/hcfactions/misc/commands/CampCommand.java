package live.ghostly.hcfactions.misc.commands;

import live.ghostly.hcfactions.factions.Faction;
import live.ghostly.hcfactions.factions.claims.Claim;
import live.ghostly.hcfactions.factions.events.player.PlayerInitiateFactionTeleportEvent;
import live.ghostly.hcfactions.factions.type.PlayerFaction;
import live.ghostly.hcfactions.factions.type.SystemFaction;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.profile.teleport.ProfileTeleportTask;
import live.ghostly.hcfactions.profile.teleport.ProfileTeleportType;
import live.ghostly.hcfactions.util.LocationSerialization;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.Style;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CampCommand extends PluginCommand {
    public static Location nearestSafeLocation(Location origin) {
        Claim landBoard = Claim.getProminentClaimAt(origin);

        if (landBoard == null) {
            return (getActualHighestBlock(origin.getBlock()).getLocation().add(0, 1, 0));
        }

        // Start iterating outward on both positive and negative X & Z.
        for (int xPos = 2, xNeg = -2; xPos < 250; xPos += 2, xNeg -= 2) {
            for (int zPos = 2, zNeg = -2; zPos < 250; zPos += 2, zNeg -= 2) {
                Location atPos = origin.clone().add(xPos, 0, zPos);

                // Try to find a unclaimed location with no claims adjacent
                if (Claim.getProminentClaimAt(atPos) == null && !isAdjacentClaimed(atPos)) {
                    return (getActualHighestBlock(atPos.getBlock()).getLocation().add(0, 1, 0));
                }

                Location atNeg = origin.clone().add(xNeg, 0, zNeg);

                // Try again to find a unclaimed location with no claims adjacent
                if (Claim.getProminentClaimAt(atNeg) == null && !isAdjacentClaimed(atNeg)) {
                    return (getActualHighestBlock(atNeg.getBlock()).getLocation().add(0, 1, 0));
                }
            }
        }

        return (null);
    }

    private static Block getActualHighestBlock(Block block) {
        block = block.getWorld().getHighestBlockAt(block.getLocation());

        while (block.getType() == Material.AIR && block.getY() > 0) {
            block = block.getRelative(BlockFace.DOWN);
        }

        return (block);
    }

    /**
     * @param base center block
     * @return list of all adjacent locations
     */
    private static List<Location> getAdjacent(Location base) {
        List<Location> adjacent = new ArrayList<>();

        // Add all relevant locations surrounding the base block
        for (BlockFace face : BlockFace.values()) {
            if (face != BlockFace.DOWN && face != BlockFace.UP) {
                adjacent.add(base.getBlock().getRelative(face).getLocation());
            }
        }

        return adjacent;
    }

    /**
     * @param location location to check for
     * @return if any of it's blockfaces are claimed
     */
    private static boolean isAdjacentClaimed(Location location) {
        for (Location adjacent : getAdjacent(location)) {
            if (Claim.getProminentClaimAt(adjacent) != null) {
                return true; // we found a claim on an adjacent block!
            }
        }

        return false;
    }

    @Command(name = "camp", inGameOnly = true, isAsync = false)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /camp <player>");
            return;
        }

        if (!player.hasPermission("faction.command.camp")) {
            player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
            return;
        }

        Location plocation = player.getLocation();
        Claim claimAt = Claim.getProminentClaimAt(plocation);
        if (claimAt != null) {
            Profile profile = Profile.getByPlayer(player);
            Faction factionAt = claimAt.getFaction();

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

        Player toCamp = Bukkit.getPlayer(StringUtils.join(args));

        if (toCamp == null) {
            player.sendMessage(ChatColor.RED + "No player named '" + StringUtils.join(args) + "' found online.");
            return;
        }

        if (toCamp.getName().equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.RED + "You can't camp yourself.");
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        Profile toCampProfile = Profile.getByPlayer(toCamp);

        PlayerFaction faction = profile.getFaction();
        PlayerFaction toCampFaction = toCampProfile.getFaction();

        if (faction == null) {
            player.sendMessage(this.main.getLanguageConfig().getString("ERROR.NOT_IN_FACTION"));
            return;
        }

        if (toCampFaction == null) {
            player.sendMessage(ChatColor.RED + "The player " + toCamp.getName() + " is not in a faction.");
            return;
        }

        if (faction.getOnlinePlayers().contains(toCamp)) {
            player.sendMessage(ChatColor.RED + "You can't camp your faction members.");
            return;
        }

        if (toCampProfile.getFaction() != null && faction.getAllies().contains(toCampProfile.getFaction())) {
            player.sendMessage(ChatColor.RED + "You can't camp your allies.");
            return;
        }

        if (toCampFaction.getHome() == null) {
            player.sendMessage(ChatColor.RED + toCamp.getName() + "'s faction has no home");
            return;
        }

        Location home = LocationSerialization.deserializeLocation(toCampFaction.getHome());


        Location location = nearestSafeLocation(home);

        if (location == null) {
            player.sendMessage(ChatColor.RED + "Could not find a location for camp.");
            return;
        }

        location.setDirection(player.getLocation().getDirection());

        player.sendMessage(Style.translate("&eYou will be teleported in 60 seconds..."));
        profile.setTeleportWarmup(new ProfileTeleportTask(new PlayerInitiateFactionTeleportEvent(player, null, ProfileTeleportType.CAMP_TELEPORT, 60, location.getWorld().getHighestBlockAt(location).getLocation(), player.getLocation())));
        profile.getTeleportWarmup().runTaskLaterAsynchronously(main, (60 * 20));
    }
}
