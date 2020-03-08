package live.ghostly.hcfactions.event.citadel;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.event.Event;
import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.player.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class CitadelEventListeners implements Listener {

    public CitadelEventListeners() {
        new BukkitRunnable() {
            @Override
            public void run() {

                for (Event possibleEvent : EventManager.getInstance().getEvents()) {
                    if (possibleEvent instanceof CitadelEvent && possibleEvent.isActive()) {
                        CitadelEvent citadel = (CitadelEvent) possibleEvent;

                        if (citadel.isFinished()) {
                            citadel.stop(false);
                            continue;
                        }

                        if (citadel.getCappingPlayer() != null) {
                            Player player = citadel.getCappingPlayer();

                            if (player.isDead() || !player.isValid() || !player.isOnline() || !citadel.getZone().isInside(player)) {
                                citadel.setCappingPlayer(null);
                            } else {
                                if (citadel.getDecisecondsLeft() % 600 == 0 && citadel.getDecisecondsLeft() != citadel.getCapTime() / 100 && citadel.getDecisecondsLeft() != 0) {
                                    Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KOTH.CONTESTED").replace("%KOTH%", citadel.getName()).replace("%TIME%", citadel.getTimeLeft()).replace("%PLAYER%", player.getName()));
                                }
                            }
                        } else {
                            if (!(citadel.isGrace())) {
                                for (Player player : PlayerUtility.getOnlinePlayers()) {
                                    Profile profile = Profile.getByPlayer(player);

                                    if (player.isDead()) {
                                        continue;
                                    }

                                    if (profile.getProtection() != null) {
                                        continue;
                                    }

                                    if (citadel.getZone().isInside(player)) {
                                        citadel.setCappingPlayer(player);
                                        break;
                                    }
                                }
                            }
                        }

                    }
                }

            }
        }.runTaskTimerAsynchronously(FactionsPlugin.getInstance(), 2L, 2L);
    }

}
