package live.ghostly.hcfactions.event.conquest;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.board.FactionsBoardAdapter;
import live.ghostly.hcfactions.factions.type.PlayerFaction;
import live.ghostly.hcfactions.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ConquestZone {

    private static FactionsPlugin main = FactionsPlugin.getInstance();
    @Getter
    @Setter
    private final Location firstLocation, secondLocation;
    @Getter
    @Setter
    private final Location minPos, maxPos;
    @Setter
    @Getter
    Color color;
    @Getter
    @Setter
    private int height;
    @Setter
    @Getter
    private long capTime;
    @Setter
    @Getter
    private long time;
    @Getter
    private Player capper;

    public ConquestZone(Location firstLocation, Location secondLocation, Color color) {
        this.firstLocation = firstLocation;
        this.secondLocation = secondLocation;
        this.minPos = this.getMinimum(this.firstLocation, this.secondLocation);
        this.maxPos = this.getMaximum(this.firstLocation, this.secondLocation);
        this.color = color;
        this.capTime = 30000;
    }

    public void setCapper(Player capper) {
        PlayerFaction faction = PlayerFaction.getByPlayer(this.capper);

        if (capper == null) {
            Bukkit.broadcastMessage(main.getLanguageConfig().getString("CONQUEST.KNOCKED").replace("%CONQUEST%", "Conquest").replace("%TIME%", getTimeLeft()).replace("%FACTION%", faction.getName()));

            //grace = 5000;
            time = System.currentTimeMillis();
        } else {
            faction = PlayerFaction.getByPlayer(capper);
            Bukkit.broadcastMessage(main.getLanguageConfig().getString("CONQUEST.CONTESTED").replace("%CONQUEST%", "Conquest").replace("%TIME%", getTimeLeft()).replace("%FACTION%", faction.getName()));
        }
        this.time = System.currentTimeMillis();
        this.capper = capper;
    }

    private Location getMinimum(Location loc1, Location loc2) {
        return new Location(loc1.getWorld(), (loc1.getX() < loc2.getX()) ? loc1.getX() : loc2.getX(), (loc1.getY() < loc2.getY()) ? loc1.getY() : loc2.getY(), (loc1.getZ() < loc2.getZ()) ? loc1.getZ() : loc2.getZ());
    }

    private Location getMaximum(Location loc1, Location loc2) {
        return new Location(loc1.getWorld(), (loc1.getX() > loc2.getX()) ? loc1.getX() : loc2.getX(), (loc1.getY() > loc2.getY()) ? loc1.getY() + height : loc2.getY() + height, (loc1.getZ() > loc2.getZ()) ? loc1.getZ() : loc2.getZ());
    }

    private boolean isInAABB(Location pos, Location pos2, Location pos3) {
        Location min = getMinimum(pos2, pos3);
        Location max = getMaximum(pos2, pos3);
        if (min.getBlockX() <= pos.getBlockX() && max.getBlockX() >= pos.getBlockX() && min.getBlockY() <= pos.getBlockY() && max.getBlockY() >= pos.getBlockY() && min.getBlockZ() <= pos.getBlockZ() && max.getBlockZ() >= pos.getBlockZ()) {
            return true;
        }
        return false;
    }

    public long getDecisecondsLeft() {
        if (capper == null) {
            return capTime / 100;
        } else {
            return (time + capTime - System.currentTimeMillis()) / 100;
        }
    }

    public String getTimeLeft() {

        long millis;

        if (capper == null) {
            millis = capTime;
        } else {
            millis = time + capTime - System.currentTimeMillis();
        }

        if (millis >= 3600000) {
            return DateUtil.formatTime(millis);
        } else if (millis >= 60000) {
            return DateUtil.formatTime(millis);
        } else {
            return FactionsBoardAdapter.SECONDS_FORMATTER.format(((millis) / 1000.0f)) + "s";
        }
    }

    public boolean isInside(Player player) {
        if (player.getWorld() == this.firstLocation.getWorld()) {
            Location loc = player.getLocation();
            if (isInAABB(loc, minPos, maxPos)) {
                return true;
            }
        }
        return false;
    }

    @AllArgsConstructor
    @Getter
    public enum Color {
        GREEN(ChatColor.GREEN, "Green"),
        RED(ChatColor.RED, "Red"),
        BLUE(ChatColor.BLUE, "Blue"),
        YELLOW(ChatColor.YELLOW, "Yellow"),
        LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, "Main");

        public ChatColor chatColor;
        public String name;

        public static Color getByName(String name) {
            for (Color color : values()) {
                if (color.name.equalsIgnoreCase(name)) {
                    return color;
                }
            }
            return null;
        }
    }
}
