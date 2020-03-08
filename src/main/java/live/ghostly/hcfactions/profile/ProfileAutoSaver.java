package live.ghostly.hcfactions.profile;

import live.ghostly.hcfactions.combatlogger.CombatLogger;
import live.ghostly.hcfactions.crate.Crate;
import live.ghostly.hcfactions.event.Event;
import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.event.glowstone.GlowstoneEvent;
import live.ghostly.hcfactions.event.koth.KothEvent;
import live.ghostly.hcfactions.factions.Faction;
import live.ghostly.hcfactions.mode.Mode;
import live.ghostly.hcfactions.util.player.SimpleOfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;


public class ProfileAutoSaver implements Runnable {

    private JavaPlugin plugin;

    public ProfileAutoSaver(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        Faction.save();

        try {
            SimpleOfflinePlayer.save(plugin);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Profile profile : Profile.getProfiles()) {
            profile.save();
        }

        for (Mode mode : Mode.getModes()) {
            mode.save();
        }

        for (CombatLogger logger : CombatLogger.getLoggers()) {
            logger.getEntity().remove();
        }

        for (Event event : EventManager.getInstance().getEvents()) {
            if (event instanceof KothEvent) {
                ((KothEvent) event).save();
            } else if (event instanceof GlowstoneEvent) {
                ((GlowstoneEvent) event).save();
            }
        }

        for (Crate crate : Crate.getCrates()) {
            crate.save();
        }

    }

}
