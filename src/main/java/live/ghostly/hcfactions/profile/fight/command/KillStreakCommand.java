package live.ghostly.hcfactions.profile.fight.command;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.entity.Player;

public class KillStreakCommand extends PluginCommand {
    @Command(name = "killstreak", aliases = "ks")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        player.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getStringList("KILL_STREAK.HELP_MENU").toArray(new String[FactionsPlugin.getInstance().getLanguageConfig().getStringList("KILL_STREAK.HELP_MENU").size()]));
    }
}
