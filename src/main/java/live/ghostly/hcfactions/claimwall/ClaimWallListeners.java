package live.ghostly.hcfactions.claimwall;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.factions.claims.Claim;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.profile.cooldown.ProfileCooldown;
import live.ghostly.hcfactions.profile.cooldown.ProfileCooldownType;
import live.ghostly.hcfactions.util.player.PlayerUtility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class ClaimWallListeners implements Listener {

    private FactionsPlugin main;

    public ClaimWallListeners(FactionsPlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.getCooldownByType(ProfileCooldownType.SPAWN_TAG) != null) {
            Claim entering = Claim.getProminentClaimAt(event.getTo());
            Claim leaving = Claim.getProminentClaimAt(event.getFrom());

            if (entering != null && (leaving == null || !leaving.equals(entering))) {
                if (ClaimWallType.SPAWN_TAG.isValid(entering)) {

                    if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                        player.sendMessage(ProfileCooldownType.SPAWN_TAG.getMessage());
                        player.sendMessage(main.getLanguageConfig().getString("SPAWN_TAG.PEARL_REFUNDED"));

                        ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.ENDER_PEARL);
                        if (cooldown != null) {
                            profile.getCooldowns().remove(cooldown);
                            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                        }

                    }

                    event.setCancelled(true);
                }
            }
        }

        if (profile.getProtection() != null) {
            Claim entering = Claim.getProminentClaimAt(event.getTo());
            Claim leaving = Claim.getProminentClaimAt(event.getFrom());

            if (entering != null && (leaving == null || !leaving.equals(entering))) {
                if (ClaimWallType.PVP_PROTECTION.isValid(entering)) {
                    event.setCancelled(true);
                    player.sendMessage(main.getLanguageConfig().getString("PVP_PROTECTION.CANT_ENTER").replace("%FACTION%", entering.getFaction().getName()).replace("%TIME%", profile.getProtection().getTimeLeft()));
                }
            }
        }
    }

}
