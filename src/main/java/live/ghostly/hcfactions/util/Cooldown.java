package live.ghostly.hcfactions.util;

import com.google.common.cache.CacheBuilder;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Cooldown {

    private static Map<String, Map> cooldown = new ConcurrentHashMap<String, Map>();

    public static void createCooldown(String k, long defaultTime) {
        if (cooldown.containsKey(k)) {
            throw new IllegalArgumentException("Cooldown already exists.");
        }
        cooldown.put(k, CacheBuilder.newBuilder().expireAfterWrite(defaultTime, TimeUnit.SECONDS).build().asMap());
    }

    public static Map getCooldownMap(String k) {
        if (cooldown.containsKey(k)) {
            return cooldown.get(k);
        }
        return null;
    }

    public static void addCooldown(String k, Player p, int seconds) {
        if (!cooldown.containsKey(k)) {
            throw new IllegalArgumentException(k + " does not exist");
        }
        long next = System.currentTimeMillis() + (long) seconds * 1000;
        cooldown.get(k).put(p.getUniqueId(), next);
    }

    public static boolean isOnCooldown(String k, Player p, long now) {
        return cooldown.containsKey(k) && cooldown.get(k).containsKey(p.getUniqueId())
                && now <= (Long) cooldown.get(k).get(p.getUniqueId());
    }

    public static boolean isOnCooldown(String k, Player p) {
        return cooldown.containsKey(k) && cooldown.get(k).containsKey(p.getUniqueId())
                && System.currentTimeMillis() <= (Long) cooldown.get(k).get(p.getUniqueId());
    }

    public static int getCooldownForPlayerInt(String k, Player p, long now) {
        return (int) ((Long) cooldown.get(k).get(p.getUniqueId()) - now) / 1000;
    }

    public static int getCooldownForPlayerInt(String k, Player p) {
        return (int) (
                (Long) cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis()
        ) / 1000;
    }

    public static long getCooldownForPlayerLong(String k, Player p) {
        return (int) (
                (Long) cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis()
        );
    }

    public static long getCooldownForPlayerLong(String k, Player p, long now) {
        return (Long) cooldown.get(k).get(p.getUniqueId()) - now;
    }

    public static void removeCooldown(String k, Player p) {
        if (!cooldown.containsKey(k)) {
            throw new IllegalArgumentException(k + " does not exist");
        }
        cooldown.get(k).remove(p.getUniqueId());
    }
}
