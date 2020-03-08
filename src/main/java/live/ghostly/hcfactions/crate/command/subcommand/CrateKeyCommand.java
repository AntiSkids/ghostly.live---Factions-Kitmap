package live.ghostly.hcfactions.crate.command.subcommand;

import live.ghostly.hcfactions.crate.Crate;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.Style;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CrateKeyCommand extends PluginCommand {
    @Command(name = "crate.key", aliases = {"key"}, inGameOnly = false, isAsync = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (!sender.hasPermission("hcf.command." + command.getCommand().getName())) {
            sender.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (args.length <= 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /crate key <name> <amount> <player>");
            return;
        }

        Crate crate = Crate.getByName(args[0]);
        if (crate == null) {
            sender.sendMessage(ChatColor.RED + "A crate named '" + args[0] + "' does not exist.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception exception) {
            sender.sendMessage(ChatColor.RED + "Invalid amount");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Invalid amount");
            return;
        }

        Player toGive = null;
        if (args.length == 2) {
            if (sender instanceof Player) {
                toGive = (Player) sender;
            }
        } else {
            if (args[2].equalsIgnoreCase("all") || args[2].equalsIgnoreCase("*")) {
                sender.sendMessage(ChatColor.GOLD + "You have successfully given " + ChatColor.YELLOW + amount + ChatColor.GOLD + " crate key" + (amount == 1 ? "" : "s") + " to " + ChatColor.YELLOW + "all players" + ChatColor.GOLD + ".");
                Bukkit.getOnlinePlayers().forEach(other -> {
                    other.getInventory().addItem(crate.getKey(amount));
                    sender.sendMessage(ChatColor.GOLD + "You have received " + ChatColor.YELLOW + amount + ChatColor.GOLD + " crate key" + (amount == 1 ? "" : "s") + ChatColor.GOLD + ".");
                });
                if (sender instanceof Player) {
                    Bukkit.broadcastMessage(Style.translate("&f" + sender.getName() + "&b&l has sent keys to all players."));
                }
                return;
            }

            toGive = Bukkit.getPlayer(args[2]);
        }

        if (toGive == null) {
            sender.sendMessage(ChatColor.RED + "Invalid player.");
            return;
        }


        sender.sendMessage(ChatColor.GOLD + "You have successfully given " + ChatColor.YELLOW + amount + ChatColor.GOLD + " crate key" + (amount == 1 ? "" : "s") + " to " + ChatColor.YELLOW + toGive.getName() + ChatColor.GOLD + ".");
        toGive.getInventory().addItem(crate.getKey(amount));
        if (toGive.getInventory().firstEmpty() == -1) {
            ItemStack keyStack = crate.getKey(amount);
            World world = toGive.getWorld();
            Location location = toGive.getLocation();
            Map<Integer, ItemStack> excess = toGive.getInventory().addItem(keyStack);
            for (ItemStack entry : excess.values()) {
                world.dropItemNaturally(location, entry);
            }
        }
    }
}
