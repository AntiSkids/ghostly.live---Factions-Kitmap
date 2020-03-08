package live.ghostly.hcfactions.inventory.command;


import com.mongodb.client.FindIterable;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.profile.fight.ProfileFight;
import live.ghostly.hcfactions.util.InventorySerialisation;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class LastInventoryCommand extends PluginCommand {

    @Command(name = "lastinventory", aliases = {"lastinv", "restoreinv", "restoreinventory"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (!player.hasPermission("hcf.command." + command.getCommand().getName())) {
            player.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        Profile toRestore;
        Player toRestorePlayer = null;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/" + command.getLabel() + " <player>");
            return;
        } else {

            toRestorePlayer = Bukkit.getPlayer(args[0]);

            if (toRestorePlayer != null) {
                toRestore = Profile.getByPlayer(toRestorePlayer);
            } else {
                toRestore = Profile.getByName(args[0]);
            }
        }

        if (toRestore == null) {
            player.sendMessage(ChatColor.RED + "No player named '" + args[0] + "' found.");
            return;
        }

        UUID uuid = null;
        if (args.length > 1) {
            try {
                uuid = UUID.fromString(args[1]);
            } catch (Exception ex) {
                player.sendMessage(ChatColor.RED + "Invalid query.");
                return;
            }
        }

        ItemStack[] contents = null, armor = null;
        if (!toRestore.getFights().isEmpty()) {
            for (ProfileFight oneFight : toRestore.getFights()) {
                if (oneFight.getKiller() != null && oneFight.getKiller().getName() != null && oneFight.getKiller().getName().equals(toRestore.getName()))
                    continue;
                if (uuid != null && !oneFight.getUuid().equals(uuid)) continue;
                contents = oneFight.getContents();
                armor = oneFight.getArmor();
            }
        } else {
            Document fightDocument;
            FindIterable iterable = main.getFactionsDatabase().getFights().find(eq("killed", toRestore.getUuid().toString())).sort(descending("occurred_at"));
            if (uuid == null) {
                fightDocument = (Document) iterable.first();
            } else {
                fightDocument = (Document) iterable.filter(eq("uuid", uuid.toString())).first();
            }

            if (fightDocument == null) {
                player.sendMessage(ChatColor.RED + toRestore.getName() + " has no previous inventories to restore. (document)");
                return;
            }

            try {
                contents = InventorySerialisation.itemStackArrayFromJson(fightDocument.getString("contents"));
                armor = InventorySerialisation.itemStackArrayFromJson(fightDocument.getString("armor"));
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "An error occurred when attempting to grab that inventory.");
                return;
            }
        }

        if (contents == null || armor == null) {
            player.sendMessage(ChatColor.RED + "An error occurred when attempting to grab that inventory.");
            return;
        }

        if (toRestorePlayer != null && toRestorePlayer.isOnline()) {
            toRestorePlayer.getInventory().setContents(contents);
            toRestorePlayer.getInventory().setArmorContents(armor);
            toRestorePlayer.sendMessage(ChatColor.RED + "Inventory successfully received.");
            player.sendMessage(ChatColor.RED + "Inventory successfully sent.");
        }

    }
}
