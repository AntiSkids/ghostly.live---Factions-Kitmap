package live.ghostly.hcfactions.event.sumo;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

@Getter
public class SumoPlayer {

    @Setter
    private SumoPlayerState state;
    @Getter
    private UUID uuid;
    @Setter
    private BukkitTask fightTask;
    @Setter
    private SumoPlayer fighting;
    @Setter
    private ItemStack[] armor;
    @Setter
    private ItemStack[] inventory;

    public SumoPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public enum SumoPlayerState {

        WAITING,
        FIGHTING,
        ELIMINATED,
        PREPARING,

    }

}
