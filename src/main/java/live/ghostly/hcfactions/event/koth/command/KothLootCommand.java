package live.ghostly.hcfactions.event.koth.command;

import live.ghostly.hcfactions.crate.Crate;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class KothLootCommand extends PluginCommand {

    @Command(name = "koth.loot", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/koth loot <koth>");
            return;
        }

        if (!args[0].contains("KOTH")) {
            args[0] += "KOTH";
        }

        Crate crate = Crate.getByName(args[0]);

        if (crate == null) {
            player.sendMessage(ChatColor.RED + "Please specify a valid KoTH.");
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 9 * 3, "Loot of " + crate.getName());

        crate.getItems().forEach(inventory::addItem);

        player.openInventory(inventory);
    }
}
