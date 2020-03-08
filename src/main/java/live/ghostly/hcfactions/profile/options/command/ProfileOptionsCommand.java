package live.ghostly.hcfactions.profile.options.command;

import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.entity.Player;

public class ProfileOptionsCommand extends PluginCommand {
    @Command(name = "options", aliases = "settings")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        player.openInventory(profile.getOptions().getInventory());
    }
}
