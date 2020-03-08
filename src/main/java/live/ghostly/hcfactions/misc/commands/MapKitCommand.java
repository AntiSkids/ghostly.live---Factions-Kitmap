package live.ghostly.hcfactions.misc.commands;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.enchantmentlimiter.EnchantmentLimiter;
import live.ghostly.hcfactions.potionlimiter.PotionLimiter;
import live.ghostly.hcfactions.util.ItemBuilder;
import live.ghostly.hcfactions.util.Style;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;

public class MapKitCommand implements Listener {

    public MapKitCommand() {
        Bukkit.getPluginManager().registerEvents(this, FactionsPlugin.getInstance());
    }

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/mapkit")) {

            if (event.getMessage().startsWith("/mapkit")) {
                if (!(event.getMessage().equalsIgnoreCase("/mapkit")) && event.getMessage().toCharArray()[7] != ' ') {
                    return;
                }
            }

            /*event.getPlayer().sendMessage(ChatColor.BLUE + "Enchant Limits: " +
                    ChatColor.GRAY + "Protection "
                    + EnchantmentLimiter.getInstance().getEnchantmentLimit(Enchantment.PROTECTION_ENVIRONMENTAL) +
                    ", Sharpness " + EnchantmentLimiter.getInstance().getEnchantmentLimit(Enchantment.DAMAGE_ALL) +
                    ", Power " + EnchantmentLimiter.getInstance().getEnchantmentLimit(Enchantment.ARROW_DAMAGE));*/
            event.setCancelled(true);

            Inventory inventory = Bukkit.createInventory(null, 9 * 3, ChatColor.BLUE + "MapKit");

            inventory.addItem(new ItemBuilder(Material.ENCHANTED_BOOK)
                    .name(ChatColor.GRAY + "Protection")
                    .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL,
                            EnchantmentLimiter.getInstance().getEnchantmentLimit(Enchantment.PROTECTION_ENVIRONMENTAL))
                    .build());
            inventory.addItem(new ItemBuilder(Material.ENCHANTED_BOOK)
                    .name(ChatColor.GRAY + "Sharpness")
                    .enchantment(Enchantment.DAMAGE_ALL,
                            EnchantmentLimiter.getInstance().getEnchantmentLimit(Enchantment.DAMAGE_ALL))
                    .build());
            inventory.addItem(new ItemBuilder(Material.ENCHANTED_BOOK)
                    .name(ChatColor.GRAY + "Power")
                    .enchantment(Enchantment.ARROW_DAMAGE,
                            EnchantmentLimiter.getInstance().getEnchantmentLimit(Enchantment.ARROW_DAMAGE))
                    .build());

            PotionLimiter potionLimiter = PotionLimiter.getInstance();

            inventory.addItem(new ItemBuilder(Material.POTION)
                    .durability(8193)
                    .name(ChatColor.LIGHT_PURPLE + "Regeneration")
                    .lore(Style.translate(potionLimiter.isBlocked(8193) ? "&cBlocked" : "&aAllowed"))
                    .build());
            inventory.addItem(new ItemBuilder(Material.POTION)
                    .durability(16388)
                    .name(ChatColor.DARK_GREEN + "Posion")
                    .lore(Style.translate(potionLimiter.isBlocked(16388) ? "&cBlocked" : "&aAllowed"))
                    .build());
            inventory.addItem(new ItemBuilder(Material.POTION)
                    .durability(16392)
                    .name(ChatColor.DARK_GRAY + "Weakness")
                    .lore(Style.translate(potionLimiter.isBlocked(16392) ? "&cBlocked" : "&aAllowed"))
                    .build());
            inventory.addItem(new ItemBuilder(Material.POTION)
                    .durability(8201)
                    .name(ChatColor.RED + "Strength")
                    .lore(Style.translate(potionLimiter.isBlocked(8201) ? "&cBlocked" : "&aAllowed"))
                    .build());
            inventory.addItem(new ItemBuilder(Material.POTION)
                    .durability(8236)
                    .name(ChatColor.DARK_RED + "Instant Damage")
                    .lore(Style.translate(potionLimiter.isBlocked(8236) ? "&cBlocked" : "&aAllowed"))
                    .build());

            event.getPlayer().openInventory(inventory);
        }
    }

}
