package live.ghostly.hcfactions.event.conquest.selection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import live.ghostly.hcfactions.event.conquest.ConquestZone;
import live.ghostly.hcfactions.factions.claims.ClaimPillar;
import live.ghostly.hcfactions.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConquestSelection {


    private static Map<UUID, ConquestSelection> selectionMap = Maps.newHashMap();

    @Getter
    private List<ConquestZone> conquestZones;
    @Getter
    private Player player;
    @Getter
    @Setter
    private ConquestZone.Color color;
    @Getter
    @Setter
    private ClaimPillar[] pillars;

    public ConquestSelection(Player player) {
        this.conquestZones = Lists.newArrayList();
        this.player = player;
        selectionMap.put(player.getUniqueId(), this);
    }

    public static ItemStack getWand() {
        return new ItemBuilder(Material.DIAMOND_HOE).name(ChatColor.GREEN + "Conquest Zone Selection").lore(Arrays.asList(
                "&aLeft click the ground&7 to set the &afirst&7 point.",
                "&aRight click the ground&7 to set the &asecond&7 point.",
                "&aSneak and left click the air&7 to confirm zone once both points set.",
                "&aRight click the air twice&7 to clear your selection."
        )).build();
    }

    public static ConquestSelection getByPlayer(Player player) {
        return selectionMap.get(player.getUniqueId());
    }

    public void claim() {
        if (color == null) {
            player.sendMessage("Conquest Zone not found");
            return;
        }
        if (pillars[0] == null) {
            player.sendMessage("First location not found");
            return;
        }
        if (pillars[2] == null) {
            player.sendMessage("Second location not found");
            return;
        }

        this.conquestZones.add(new ConquestZone(pillars[0].getOriginalLocation(), pillars[1].getOriginalLocation(), color));
    }

}
