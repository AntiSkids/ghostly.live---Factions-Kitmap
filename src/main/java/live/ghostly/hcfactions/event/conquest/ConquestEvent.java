package live.ghostly.hcfactions.event.conquest;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.event.Event;
import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.factions.type.PlayerFaction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ConquestEvent implements Event {

    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private static EventManager manager = EventManager.getInstance();

    @Getter
    private final UUID uuid;
    @Getter
    private final String name;
    @Getter
    private final List<ConquestZone> zones;
    @Getter
    @Setter
    private boolean active;
    @Setter
    @Getter
    private Map<String, Integer> factionsCappers;
    private PlayerFaction winnerFaction;
    @Getter
    @Setter
    private int fase = 1;
    @Getter
    private long startedAt;

    public ConquestEvent(UUID uuid, String name, List<ConquestZone> zones) {
        this.uuid = uuid;
        this.name = name;
        this.zones = zones;
        this.active = false;

        manager.getEvents().add(this);
    }

    public ConquestEvent(String name, List<ConquestZone> zones) {
        this(UUID.randomUUID(), name, zones);
    }

    public void addFactionPoint(PlayerFaction faction) {
        if (this.factionsCappers.containsKey(faction.getName())) {
            this.factionsCappers.put(faction.getName(), getFactionPoints(faction) + 1);
        } else {
            this.factionsCappers.put(faction.getName(), 1);
        }
    }

    public int getFactionPoints(PlayerFaction faction) {
        return this.factionsCappers.get(faction.getName());
    }


    public void start() {
        this.active = true;
        this.startedAt = System.currentTimeMillis();
        Bukkit.broadcastMessage(main.getLanguageConfig().getString("CONQUEST.START").replace("%CONQUEST%", name));
    }

    public void stop(boolean force, PlayerFaction faction) {

        if (force) {
            Bukkit.broadcastMessage(main.getLanguageConfig().getString("CONQUEST.STOP").replace("%CONQUEST%", "Conquest"));
        } else {
            this.winnerFaction = faction;
            Bukkit.broadcastMessage(main.getLanguageConfig().getString("CONQUEST.STOP_WINNER").replace("%CONQUEST%", "Conquest").replace("%Faction%", winnerFaction.getName()));
            //this.cappingPlayer.sendMessage(ChatColor.GREEN + "You have received (1) KOTH Crate Key.");
            //Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate give to " + this.cappingPlayer.getName() + " KOTH 1");
        }

        this.active = false;
        zones.forEach(zone -> {
            zone.setCapTime(30000);
            zone.setCapper(null);
        });
    }

    @Override
    public List<String> getScoreboardText() {
        return null;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public boolean isFaseTwo() {
        return TimeUnit.MILLISECONDS.toMinutes(startedAt - System.currentTimeMillis()) == 30;
    }
}
