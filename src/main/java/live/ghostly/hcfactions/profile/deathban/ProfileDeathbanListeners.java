package live.ghostly.hcfactions.profile.deathban;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.mode.Mode;
import live.ghostly.hcfactions.mode.ModeType;
import live.ghostly.hcfactions.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ProfileDeathbanListeners implements Listener {

    private static FactionsPlugin main = FactionsPlugin.getInstance();

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = Profile.getByPlayer(player);

        for (Mode mode : Mode.getModes()) {
            if (mode.getModeType() == ModeType.EOTW && mode.isActive()) {
                if (profile.getDeathban() == null) {
                    profile.setDeathban(new ProfileDeathban(1000000));
                    //player.kickPlayer(ProfileDeathban.KICK_MESSAGE.replace("%TIME%", profile.getDeathban().getTimeLeft()));
                    player.kickPlayer(ChatColor.YELLOW + "Come back for SOTW!");
                    return;
                }
            }
        }

        if (!main.isKitmapMode()) {
            if (ProfileDeathban.getDuration(player) > 0 && profile.getDeathban() == null) {
                profile.setDeathban(new ProfileDeathban(ProfileDeathban.getDuration(player)));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isOnline()) {
                            profile.setDeathban(new ProfileDeathban(ProfileDeathban.getDuration(player)));
                            player.kickPlayer(ProfileDeathban.KICK_MESSAGE.replace("%TIME%", profile.getDeathban().getTimeLeft()));
                        }
                    }
                }.runTaskLater(main, 20 * 5L);
            }
        }
    }

    /*@EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        for (Mode mode : Mode.getModes()) {
            if (mode.getModeType() == ModeType.EOTW && mode.isActive()) {
                if (profile.getDeathban() == null && ProfileDeathban.getDuration(player) > 0) {
                    profile.setDeathban(new ProfileDeathban(1000000));
                    //player.kickPlayer(ProfileDeathban.KICK_MESSAGE.replace("%TIME%", profile.getDeathban().getTimeLeft()));
                    player.kickPlayer(ChatColor.YELLOW + "Come back for SOTW!");
                    return;
                }
            }
        }

        if (!main.isKitmapMode()) {
            if (profile.getDeathban() == null && ProfileDeathban.getDuration(player) > 0) {
                profile.setDeathban(new ProfileDeathban(ProfileDeathban.getDuration(player)));
                player.kickPlayer(ProfileDeathban.KICK_MESSAGE.replace("%TIME%", profile.getDeathban().getTimeLeft()));
            }
        }
    }*/

}