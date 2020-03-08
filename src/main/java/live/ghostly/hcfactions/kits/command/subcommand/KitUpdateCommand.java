package live.ghostly.hcfactions.kits.command.subcommand;

import live.ghostly.hcfactions.kits.Kit;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.Collections;

public class KitUpdateCommand extends PluginCommand {

    @Command(name = "kit.update")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (!player.hasPermission("hcf.command." + command.getCommand().getName())) {
            player.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }


        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /kit update <name>");
            return;
        }

        String name = StringUtils.join(args);
        Kit kit = Kit.getByName(name);

        if (kit == null) {
            player.sendMessage(ChatColor.RED + "A kit named '" + name + "' does not exist.");
            return;
        }

        kit.getItems().clear();

        Collections.addAll(kit.getItems(), player.getInventory().getContents());
        Collections.addAll(kit.getItems(), player.getInventory().getArmorContents());

        kit.save();
    }
}
