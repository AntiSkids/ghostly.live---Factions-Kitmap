package live.ghostly.hcfactions.event.citadel.command;

import live.ghostly.hcfactions.crate.Crate;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CitadelLootCommand extends PluginCommand {

    @Command(name = "citadel.loot", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            Crate crate = Crate.getByName("citadel");

            if (crate == null) {
                player.sendMessage(ChatColor.RED + "Please specify a valid Citadel.");
                return;
            }

            Inventory inventory = Bukkit.createInventory(null, 9 * 3, "Loot of " + crate.getName());

            crate.getItems().forEach(inventory::addItem);

            player.openInventory(inventory);
            return;
        }

        Crate crate = Crate.getByName(args[0]);

        if (crate == null) {
            player.sendMessage(ChatColor.RED + "Please specify a valid Citadel.");
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 9 * 3, "Loot of " + crate.getName());

        crate.getItems().forEach(inventory::addItem);

        player.openInventory(inventory);
    }
}
