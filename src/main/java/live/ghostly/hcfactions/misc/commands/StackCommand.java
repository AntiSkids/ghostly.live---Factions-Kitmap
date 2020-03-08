package live.ghostly.hcfactions.misc.commands;

import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.entity.Player;

public class StackCommand extends PluginCommand {

    @Command(name = "stack", aliases = {"more"}, inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        if (!player.hasPermission("hcf.command." + command.getCommand().getName())) {
            player.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        player.getInventory().getItemInHand().setAmount(64);
    }


}
