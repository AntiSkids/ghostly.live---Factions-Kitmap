package live.ghostly.hcfactions.event.conquest;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.event.Event;
import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.factions.type.PlayerFaction;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.NumberUtil;
import live.ghostly.hcfactions.util.player.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ConquestEventListeners {

    public ConquestEventListeners() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Event possibleEvent : EventManager.getInstance().getEvents()) {
                    if (possibleEvent instanceof ConquestEvent && possibleEvent.isActive()) {
                        ConquestEvent conquestEvent = (ConquestEvent) possibleEvent;

                        /*if (conquestEvent.isFinished()) {
                            conquestEvent.stop(false);
                            continue;
                        }*/
                        if (conquestEvent.isFaseTwo()) {
                            conquestEvent.setFase(2);
                            Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("CONQUEST.NEW_FASE").replace("%CONQUEST%", conquestEvent.getName()).replace("%FASE%", NumberUtil.toRomanNumeral(conquestEvent.getFase())));
                        }

                        for (ConquestZone conquestZone : conquestEvent.getZones()) {
                            if (conquestZone.getCapper() != null) {
                                Player player = conquestZone.getCapper();
                                if (player.isDead() || !player.isValid() || !player.isOnline() || !conquestZone.isInside(player)) {
                                    conquestZone.setCapper(null);
                                } else {
                                    PlayerFaction faction = PlayerFaction.getByPlayer(player);

                                    if (conquestEvent.getFactionsCappers().containsKey(faction.getName()) && conquestEvent.getFase() == 1) {
                                        continue;
                                    }

                                    if (conquestZone.getDecisecondsLeft() % 600 == 0 && conquestZone.getDecisecondsLeft() != conquestZone.getCapTime() / 100 && conquestZone.getDecisecondsLeft() != 0) {
                                        conquestEvent.addFactionPoint(faction);
                                        Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("CONQUEST.CONTESTED").replace("%CONQUEST%", conquestEvent.getName()).replace("%POINTS%", String.valueOf(conquestEvent.getFactionPoints(faction))).replace("%PLAYER%", player.getName()));
                                        if (conquestEvent.getFactionPoints(faction) == 300) {
                                            //Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("CONQUEST.NEW_FASE").replace("%CONQUEST%", conquestEvent.getName()).replace("%FASE%", String.valueOf(conquestEvent.getFactionPoints(faction))));
                                            conquestEvent.stop(false, faction);
                                        }
                                        conquestZone.setCapTime(30000);
                                    }
                                }
                            } else {
                                //if (!(conquestZone.isGrace())) {
                                for (Player player : PlayerUtility.getOnlinePlayers()) {
                                    Profile profile = Profile.getByPlayer(player);
                                    PlayerFaction faction = PlayerFaction.getByPlayer(player);

                                    if (player.isDead()) {
                                        continue;
                                    }

                                    if (profile.getProtection() != null) {
                                        continue;
                                    }


                                    if (faction == null) {
                                        continue;
                                    }

                                    /*if (ModPlayer.getMods().containsKey(player.getUniqueId())) {
                                        ModPlayer mod = ModPlayer.getByPlayer(player);
                                        if (mod.isActivated()) {
                                            continue;
                                        }
                                    }*/

                                    if (conquestZone.isInside(player)) {
                                        conquestZone.setCapper(player);
                                        break;
                                    }
                                }
                                //}
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(FactionsPlugin.getInstance(), 2L, 2L);
    }

}
