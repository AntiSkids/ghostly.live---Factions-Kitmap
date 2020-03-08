package live.ghostly.hcfactions.factions.commands.leader;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.claimwall.ClaimWallType;
import live.ghostly.hcfactions.factions.Faction;
import live.ghostly.hcfactions.factions.claims.Claim;
import live.ghostly.hcfactions.factions.commands.FactionCommand;
import live.ghostly.hcfactions.factions.events.player.PlayerDisbandFactionEvent;
import live.ghostly.hcfactions.factions.type.PlayerFaction;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import live.ghostly.hcfactions.util.player.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class FactionDisbandCommand extends FactionCommand {
    @Command(name = "f.disband", aliases = {"faction.disband", "factions.disband"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Profile profile = Profile.getByPlayer(player);
        PlayerFaction playerFaction;

        if (command.getArgs().length >= 1 && player.hasPermission("hcf.command." + command.getCommand().getName())) {
            String name = command.getArgs(0);


            Faction faction = PlayerFaction.getAnyByString(name);
            if (faction != null) {
                if (faction instanceof PlayerFaction) {
                    playerFaction = (PlayerFaction) faction;
                } else {
                    player.sendMessage(langConfig.getString("SYSTEM_FACTION.DELETED").replace("%NAME%", faction.getName()));

                    Faction.getFactions().remove(faction);

                    Set<Claim> claims = new HashSet<>(faction.getClaims());
                    for (Claim claim : claims) {
                        claim.remove();
                    }

                    main.getFactionsDatabase().getDatabase().getCollection("systemFactions").deleteOne(eq("uuid", faction.getUuid().toString()));
                    return;
                }
            } else {
                player.sendMessage(langConfig.getString("ERROR.NO_FACTIONS_FOUND").replace("%NAME%", name));
                return;
            }
        } else {
            playerFaction = profile.getFaction();

            if (playerFaction == null) {
                player.sendMessage(langConfig.getString("ERROR.NOT_IN_FACTION"));
                return;
            }

            if (!playerFaction.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(langConfig.getString("ERROR.NOT_LEADER"));
                return;
            }
        }

        for (UUID member : playerFaction.getAllPlayerUuids()) {
            Profile memberProfile = Profile.getByUuid(member);

            if (memberProfile != null && memberProfile.getFaction().equals(playerFaction)) {
                memberProfile.setFaction(null);
            }
        }

        profile.setBalance(profile.getBalance() + playerFaction.getBalance());

        Bukkit.getPluginManager().callEvent(new PlayerDisbandFactionEvent(player, playerFaction));

        Bukkit.broadcastMessage(langConfig.getString("ANNOUNCEMENTS.FACTION_DISBANDED").replace("%PLAYER%", player.getName()).replace("%NAME%", playerFaction.getName()));
        Faction.getFactions().remove(playerFaction);

        Bukkit.getScheduler().runTaskAsynchronously(FactionsPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                main.getFactionsDatabase().getDatabase().getCollection("playerFactions").deleteOne(eq("uuid", playerFaction.getUuid().toString()));
            }
        });

        for (PlayerFaction ally : playerFaction.getAllies()) {
            ally.getAllies().remove(playerFaction);
        }

        Set<Claim> claims = new HashSet<>(playerFaction.getClaims());
        this.removeWalls(claims);
        for (Claim claim : claims) {
            claim.remove();
        }
    }

    private void removeWalls(Set<Claim> claims) {

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : PlayerUtility.getOnlinePlayers()) {
                    Profile profile = Profile.getByPlayer(player);

                    for (Claim claim : claims) {

                        for (ClaimWallType type : ClaimWallType.values()) {
                            if (claim == null) {
                                continue;
                            }

                            if (!(type.isValid(claim))) {
                                continue;
                            }

                            if (claim.getBorder() == null) {
                                continue;
                            }

                            int min = player.getLocation().getBlockY() - (type.getRange() / 2);
                            int max = player.getLocation().getBlockY() + (type.getRange() / 2);

                            for (Location location : claim.getBorder()) {
                                for (int i = min - (20); i < max + (20); i++) {
                                    location.setY(i);
                                    if (location.getBlock().isEmpty()) {
                                        if (profile.getWalls().containsKey(location)) {
                                            profile.getWalls().get(location).hide(player);
                                            profile.getWalls().remove(location);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(this.main);
    }
}
