package live.ghostly.hcfactions.event.conquest.selection;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.event.koth.procedure.KothCreateProcedure;
import live.ghostly.hcfactions.factions.claims.ClaimPillar;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ConquestSelectionListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ConquestSelection conquestSelection = ConquestSelection.getByPlayer(player);

        if (conquestSelection == null && event.getItem() != null && event.getItem().isSimilar(ConquestSelection.getWand())) {
            player.getInventory().removeItem(event.getItem());
            return;
        }

        if (conquestSelection != null && event.getItem() != null && event.getItem().isSimilar(KothCreateProcedure.getWand())) {
            event.setCancelled(true);
            if (action == Action.LEFT_CLICK_BLOCK) {

                ClaimPillar claimPillar = conquestSelection.getPillars()[0];
                if (claimPillar != null) {
                    claimPillar.remove();
                }

                player.sendMessage(" ");
                player.sendMessage(ChatColor.YELLOW + "First position set.");
                player.sendMessage(" ");

                conquestSelection.getPillars()[0] = new ClaimPillar(player, event.getClickedBlock().getLocation()).show(Material.LAPIS_BLOCK, 0);
            }

            if (action == Action.RIGHT_CLICK_BLOCK) {

                ClaimPillar claimPillar = conquestSelection.getPillars()[1];
                if (claimPillar != null) {
                    claimPillar.remove();
                }

                player.sendMessage(" ");
                player.sendMessage(ChatColor.YELLOW + "Second position set.");
                player.sendMessage(" ");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        conquestSelection.getPillars()[1] = new ClaimPillar(player, event.getClickedBlock().getLocation()).show(Material.LAPIS_BLOCK, 0);
                    }
                }.runTaskLaterAsynchronously(FactionsPlugin.getInstance(), 2L);
            }

            if (action == Action.LEFT_CLICK_AIR && player.isSneaking()) {
                for (ClaimPillar claimPillar : conquestSelection.getPillars()) {
                    if (claimPillar == null) {
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.RED + "Conquest zone not defined.");
                        player.sendMessage(" ");
                        return;
                    }
                }

                for (ClaimPillar claimPillar : conquestSelection.getPillars()) {
                    claimPillar.remove();
                }

                player.getInventory().removeItem(ConquestSelection.getWand());

                conquestSelection.claim();
                conquestSelection.setPillars(null);
            }
        }


    }

}
