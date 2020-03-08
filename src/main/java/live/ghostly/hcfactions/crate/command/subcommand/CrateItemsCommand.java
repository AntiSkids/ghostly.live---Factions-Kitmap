package live.ghostly.hcfactions.crate.command.subcommand;


import live.ghostly.hcfactions.crate.Crate;
import live.ghostly.hcfactions.util.ItemBuilder;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class CrateItemsCommand extends PluginCommand {
    @Command(name = "crate.items")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (!player.hasPermission("hcf.command." + command.getCommand().getName())) {
            player.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /crate items <name>");
            return;
        }

        String name = StringUtils.join(args);
        Crate crate = Crate.getByName(name);

        if (crate == null) {
            player.sendMessage(ChatColor.RED + "A crate named '" + name + "' does not exist.");
            return;
        }

        Inventory inventory = Bukkit.createInventory(player, 9 * 6, ChatColor.RED + "Items - 1/1");

        inventory.setItem(0, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED.toString()).build());
        inventory.setItem(8, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED.toString()).build());
        inventory.setItem(4, new ItemBuilder(Material.PAPER).name(ChatColor.RED + "Page 1/1").lore(Arrays.asList(ChatColor.YELLOW + "Crate: " + ChatColor.RED + crate.getName())).build());

        for (int i = 0; i < crate.getItems().size(); i++) {
            inventory.setItem(9 + i, crate.getItems().get(i));
        }

        player.openInventory(inventory);
    }
}
