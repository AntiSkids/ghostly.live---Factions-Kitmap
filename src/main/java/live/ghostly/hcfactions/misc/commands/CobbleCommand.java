package live.ghostly.hcfactions.misc.commands;

import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CobbleCommand extends PluginCommand {

    @Command(name = "cobble", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Profile profile = Profile.getByPlayer(player);

        if (profile == null) {
            return;
        }

        profile.setCobble(!profile.isCobble());

        player.sendMessage(ChatColor.YELLOW + "You have toggled cobble " + (profile.isCobble() ? "on" : "off") + ".");

    }


}
