package live.ghostly.hcfactions.util.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class PlayerUtility {

    public static Set<String> getConvertedUuidSet(Set<UUID> uuids) {
        Set<String> toReturn = new HashSet<>();

        for (UUID uuid : uuids) {
            toReturn.add(uuid.toString());
        }

        return toReturn;
    }

    public static List<Player> getOnlinePlayers() {
        return new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
    }

    public static Player getFinalAttacker(final EntityDamageEvent ede, final boolean ignoreSelf) {
        Player attacker = null;
        if (ede instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) ede;
            final Entity damager = event.getDamager();
            if (event.getDamager() instanceof Player) {
                attacker = (Player) damager;
            } else if (event.getDamager() instanceof Projectile) {
                final Projectile projectile = (Projectile) damager;
                final ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Player) {
                    attacker = (Player) shooter;
                }
            }
            if (attacker != null && ignoreSelf && event.getEntity().equals(attacker)) {
                attacker = null;
            }
        }
        return attacker;
    }

}
