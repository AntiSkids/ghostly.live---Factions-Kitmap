package live.ghostly.hcfactions.event.sumo.listeners;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.event.sumo.SumoEvent;
import live.ghostly.hcfactions.event.sumo.SumoPlayer;
import live.ghostly.hcfactions.factions.type.SystemFaction;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.LocationSerialization;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class SumoListeners implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damaged = (Player) event.getEntity();
        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if (!sumoEvent.getPlayers().containsKey(damaged.getUniqueId())) {
            return;
        }

        SumoPlayer damagedSumo = sumoEvent.getPlayer(damaged);

        if (damagedSumo.getState() != SumoPlayer.SumoPlayerState.FIGHTING) {
            event.setCancelled(true);
        }
    }

    /*@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event){
        if(!(event.getEntity() instanceof Player)){
            return;
        }
        if(!(event.getDamager() instanceof Player)){
            return;
        }

        Player damaged = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if(!sumoEvent.getPlayers().containsKey(damaged.getUniqueId()) || !sumoEvent.getPlayers().containsKey(damager.getUniqueId())){
            return;
        }
        SumoPlayer damagedSumo = sumoEvent.getPlayer(damaged);
        SumoPlayer damagerSumo = sumoEvent.getPlayer(damager);

        if(damagedSumo.getState() != SumoPlayer.SumoPlayerState.FIGHTING){
            event.setCancelled(true);
            return;
        }

        if(damagerSumo.getState() == SumoPlayer.SumoPlayerState.FIGHTING && damagedSumo.getState() == SumoPlayer.SumoPlayerState.FIGHTING){
            event.setCancelled(false);
            event.setDamage(0);
        }
    }*/

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if (!sumoEvent.getPlayers().containsKey(player.getUniqueId())) {
            return;
        }

        SumoPlayer sumoPlayer = sumoEvent.getPlayer(player);

        if (sumoPlayer.getState() == SumoPlayer.SumoPlayerState.WAITING) {

            ItemStack item = event.getItem();

            if (item == null || item.getType() != Material.FIREBALL) {
                return;
            }
            event.setCancelled(true);
            sumoEvent.leave(player, false);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if (!sumoEvent.getPlayers().containsKey(player.getUniqueId())) {
            return;
        }
        if (event.getMessage().startsWith("/team home") ||
                event.getMessage().startsWith("/t home") ||
                event.getMessage().startsWith("/faction home") ||
                event.getMessage().startsWith("/f home") ||
                event.getMessage().startsWith("/spawn") ||
                event.getMessage().startsWith("/f stuck") ||
                event.getMessage().startsWith("/faction stuck") ||
                event.getMessage().startsWith("/team stuck") ||
                event.getMessage().startsWith("/camp") ||
                event.getMessage().startsWith("/t stuck")) {
            event.setCancelled(true);
        }


    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if (!sumoEvent.getPlayers().containsKey(player.getUniqueId())) {
            return;
        }
        sumoEvent.leave(player, true);
    }

    @EventHandler
    public void onQuit(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if (!sumoEvent.getPlayers().containsKey(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.isTeleport()) {
            SystemFaction spawn = SystemFaction.getByName("Spawn");

            if (spawn == null || spawn.getHome() == null) {
                player.teleport(player.getWorld().getSpawnLocation());
                return;
            }

            player.teleport(LocationSerialization.deserializeLocation(spawn.getHome()));
            profile.setTeleport(false);
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if (!sumoEvent.getPlayers().containsKey(player.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if (!sumoEvent.getPlayers().containsKey(player.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }
}
