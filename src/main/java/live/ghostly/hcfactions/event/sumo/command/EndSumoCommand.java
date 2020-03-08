package live.ghostly.hcfactions.event.sumo.command;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.event.sumo.SumoEvent;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.Style;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.entity.Player;

public class EndSumoCommand extends PluginCommand {

    @Command(name = "sumo.end")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (!player.hasPermission("sumo.end")) {
            player.sendMessage(Style.translate("&cNo permissions"));
            return;
        }

        if (!FactionsPlugin.getInstance().isKitmapMode()) {
            player.sendMessage(Style.translate("&cOnly available in kitmap"));
            return;
        }

        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if (!sumoEvent.isStarted()) {
            player.sendMessage(Style.translate("&cSumo event not started."));
            return;
        }

        sumoEvent.end();
    }
}
