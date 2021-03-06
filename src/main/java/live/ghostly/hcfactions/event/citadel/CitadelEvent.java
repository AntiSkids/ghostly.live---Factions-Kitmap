package live.ghostly.hcfactions.event.citadel;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.event.Event;
import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.event.EventZone;
import live.ghostly.hcfactions.util.DateUtil;
import live.ghostly.hcfactions.util.LocationSerialization;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class CitadelEvent implements Event {

    private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");
    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private static EventManager manager = EventManager.getInstance();

    @Getter
    private final UUID uuid;
    @Getter
    private final String name;
    @Getter
    private final int height;
    @Getter
    private final EventZone zone;
    @Getter
    @Setter
    private long time;
    @Getter
    @Setter
    private long capTime;
    @Getter
    @Setter
    private long grace;
    @Getter
    @Setter
    private boolean active;
    @Getter
    @Setter
    private Player cappingPlayer;

    public CitadelEvent(UUID uuid, String name, int height, EventZone zone) {
        this.uuid = uuid;
        this.name = name;
        this.zone = zone;
        this.height = height;

        manager.getEvents().add(this);
    }


    public CitadelEvent(String name, int height, EventZone zone) {
        this(UUID.randomUUID(), name, height, zone);
    }

    public static void load() {
        MongoCollection collection = main.getFactionsDatabase().getCitadels();
        MongoCursor cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document document = (Document) cursor.next();
            UUID uuid = UUID.fromString(document.getString("uuid"));
            String name = document.getString("name");
            int height = document.getInteger("height");

            JsonArray zoneArray = new JsonParser().parse(document.getString("zone")).getAsJsonArray();
            Location firstLocation = LocationSerialization.deserializeLocation(zoneArray.get(0).getAsString());
            Location secondLocation = LocationSerialization.deserializeLocation(zoneArray.get(1).getAsString());

            EventZone zone = new EventZone(firstLocation, secondLocation);
            zone.setHeight(height);
            new CitadelEvent(uuid, name, height, zone);
        }
    }

    public void start(long capTime) {
        this.capTime = capTime;
        this.active = true;

        Bukkit.broadcastMessage(main.getLanguageConfig().getString("CITADEL.START").replace("%CITADEL%", name).replace("%TIME%", getTimeLeft()));
    }

    public void stop(boolean force) {

        if (force || cappingPlayer == null) {
            Bukkit.broadcastMessage(main.getLanguageConfig().getString("CITADEL.STOP").replace("%CITADEL%", name).replace("%TIME%", getTimeLeft()));
        } else {
            Bukkit.broadcastMessage(main.getLanguageConfig().getString("CITADEL.STOP_WINNER").replace("%CITADEL%", name).replace("%TIME%", getTimeLeft()).replace("%PLAYER%", cappingPlayer.getName()));
            this.cappingPlayer.sendMessage(ChatColor.GREEN + "You have received (1) CITADEL Crate Key.");
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ccrate key " + name + " 1 " + this.cappingPlayer.getName());
        }

        this.active = false;
        this.capTime = 0;
        this.time = 0;
        this.cappingPlayer = null;
    }

    public boolean isGrace() {
        return time + grace > System.currentTimeMillis();
    }

    public boolean isFinished() {
        return active && time + capTime - (System.currentTimeMillis() + 1000) <= 0 && cappingPlayer != null;
    }

    public void setCappingPlayer(Player player) {
        if (player == null) {
            Bukkit.broadcastMessage(main.getLanguageConfig().getString("CITADEL.KNOCKED").replace("%CITADEL%", name).replace("%TIME%", getTimeLeft()).replace("%PLAYER%", cappingPlayer.getName()));

            grace = 5000;
            time = System.currentTimeMillis();
        } else {
            Bukkit.broadcastMessage(main.getLanguageConfig().getString("CITADEL.CONTESTED").replace("%CITADEL%", name).replace("%TIME%", getTimeLeft()).replace("%PLAYER%", player.getName()));
        }

        this.cappingPlayer = player;
        this.time = System.currentTimeMillis();
    }

    public long getDecisecondsLeft() {
        if (cappingPlayer == null) {
            return capTime / 100;
        } else {
            return (time + capTime - System.currentTimeMillis()) / 100;
        }
    }

    public String getTimeLeft() {

        long millis;

        if (cappingPlayer == null) {
            millis = capTime;
        } else {
            millis = time + capTime - System.currentTimeMillis();
        }

        if (millis >= 3600000) {
            return DateUtil.formatTime(millis);
        } else if (millis >= 60000) {
            return DateUtil.formatTime(millis);
        } else {
            return SECONDS_FORMATTER.format(((millis) / 1000.0f)) + "s";
        }
    }

    @Override
    public List<String> getScoreboardText() {
        List<String> toReturn = new ArrayList<>();

        for (String line : main.getScoreboardConfig().getStringList("PLACE_HOLDER.CITADEL")) {
            line = line.replace("%CITADEL%", name);
            line = line.replace("%TIME%", getTimeLeft());

            toReturn.add(line);
        }

        return toReturn;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void remove() {
        main.getFactionsDatabase().getCitadels().deleteOne(eq("uuid", this.uuid.toString()));
        manager.getEvents().remove(this);
    }

    public void save() {
        MongoCollection collection = main.getFactionsDatabase().getCitadels();

        Document document = new Document();
        document.put("uuid", uuid.toString());
        document.put("name", name);
        document.put("height", height);

        JsonArray zoneArray = new JsonArray();
        zoneArray.add(new JsonPrimitive(LocationSerialization.serializeLocation(zone.getFirstLocation())));
        zoneArray.add(new JsonPrimitive(LocationSerialization.serializeLocation(zone.getSecondLocation())));

        document.put("zone", zoneArray.toString());

        collection.replaceOne(eq("uuid", uuid.toString()), document, new UpdateOptions().upsert(true));
    }

    private String getCitadelLocation(CitadelEvent event) {
        return (event.getZone().getCenter().getX() < 0 ? "-500" : "500") + ", " + event.getZone().getCenter().getBlockY() + ", " + (event.getZone().getCenter().getZ() < 0 ? "-500" : "500");
    }

    private String getKitMapCitadelLocation(CitadelEvent event) {
        return (event.getZone().getCenter().getX() < 0 ? "-250" : "250") + ", " + event.getZone().getCenter().getBlockY() + ", " + (event.getZone().getCenter().getZ() < 0 ? "-250" : "250");
    }

}
