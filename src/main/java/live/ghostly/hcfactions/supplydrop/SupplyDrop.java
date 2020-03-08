package live.ghostly.hcfactions.supplydrop;

import com.google.common.collect.Lists;
import live.ghostly.hcfactions.FactionsPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class SupplyDrop {

    private static final int PREPARATION_TIME = 2;
    private static FactionsPlugin main = FactionsPlugin.getInstance();
    @Getter
    private final Location location;
    @Getter
    @Setter
    private SupplyDropState state;
    private List<ItemStack> items = Lists.newArrayList();

    public SupplyDrop(Location location) {
        this.location = location;
        this.state = SupplyDropState.PREPARING;

        new BukkitRunnable() {
            @Override
            public void run() {
                Block block = new Location(location.getWorld(), location.getX(), location.getWorld().getMaxHeight(), location.getZ()).getBlock();
                Chest chest = (Chest) block.getState();
                int cacheNumber;
                for (int i = 0; i < items.size(); i++) {
                    cacheNumber = new Random().nextInt(items.size());
                    chest.getInventory().setItem(i, items.get(cacheNumber));
                }
                location.getWorld().spawnFallingBlock(block.getLocation(), Material.CHEST, (byte) 0);
            }
        }.runTaskLater(main, PREPARATION_TIME);
    }
}
