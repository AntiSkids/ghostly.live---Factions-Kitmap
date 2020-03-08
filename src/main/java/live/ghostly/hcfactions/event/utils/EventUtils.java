package live.ghostly.hcfactions.event.utils;

import com.google.common.collect.Lists;
import live.ghostly.hcfactions.util.DateTimeFormats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EventUtils {

    public static ItemStack getEventSign(String playerName, String kothName) {
        ItemStack stack = new ItemStack(Material.SIGN, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Event Sign");
        meta.setLore(Lists.newArrayList(
                ChatColor.DARK_RED + playerName,
                ChatColor.YELLOW + "captured by",
                ChatColor.GREEN + kothName,
                DateTimeFormats.MNT_DAY_HR_MIN_AMPH.format(System.currentTimeMillis())));
        stack.setItemMeta(meta);
        return stack;
    }

}
