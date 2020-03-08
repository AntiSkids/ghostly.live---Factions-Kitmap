package live.ghostly.hcfactions.event.schedule;

import live.ghostly.hcfactions.event.Event;
import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.event.citadel.CitadelEvent;
import live.ghostly.hcfactions.event.koth.KothEvent;
import live.ghostly.hcfactions.util.DateUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class Schedule {

    private String name;
    private String formatDay;
    private long eventTime;
    private long announceTime;
    private String runTime;

    public Schedule(long eventTime, String runTime, String name, String formatDay) {
        this.name = name;
        this.eventTime = eventTime;
        this.announceTime = this.eventTime - 30 * 60 * 1000L;
        this.runTime = runTime;
        this.formatDay = formatDay;
    }

    public void runEvent() {

        if (System.currentTimeMillis() > this.eventTime) {

            Event event = EventManager.getInstance().getByName(name);
            if (event != null && event instanceof KothEvent && !event.isActive()) {
                KothEvent koth = (KothEvent) event;

                try {
                    long capTime = System.currentTimeMillis() - DateUtil.parseDateDiff(this.runTime, false);
                    koth.start(capTime);
                    setNextEventTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (event != null && event instanceof CitadelEvent && !event.isActive()) {
                CitadelEvent citadel = (CitadelEvent) event;

                try {
                    long capTime = System.currentTimeMillis() - DateUtil.parseDateDiff(this.runTime, false);
                    citadel.start(capTime);
                    setNextEventTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (System.currentTimeMillis() > this.announceTime) {
            Event event = EventManager.getInstance().getByName(name);

            if (event != null && event instanceof KothEvent && !event.isActive()) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "Scheduled KoTH Event " + ChatColor.GRAY + "(" + name + ") " + ChatColor.YELLOW + "will start in 30 minutes.");
                setNextAnnounceTime();
            }
            if (event != null && event instanceof CitadelEvent && !event.isActive()) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "Scheduled Citadel Event " + ChatColor.GRAY + "(" + name + ") " + ChatColor.YELLOW + "will start in 30 minutes.");
                setNextAnnounceTime();
            }
        }
    }

    public String getFormatDay() {
        return formatDay;
    }

    public long getEventTime() {
        return this.eventTime;
    }

    public String getName() {
        return name;
    }

    private void setNextEventTime() {
        this.eventTime += 1000 * 60 * 60 * 24 * 7;
    }

    private void setNextAnnounceTime() {
        this.announceTime += 1000 * 60 * 60 * 24 * 7;
    }
}
