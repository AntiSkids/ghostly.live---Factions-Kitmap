package live.ghostly.hcfactions.event.citadel.command;

import live.ghostly.hcfactions.event.Event;
import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.event.citadel.CitadelEvent;
import live.ghostly.hcfactions.event.schedule.Schedule;
import live.ghostly.hcfactions.event.schedule.ScheduleHandler;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.apache.commons.lang.time.FastDateFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.TimeZone;

public class CitadelCommand extends PluginCommand {

    @Command(name = "citadel", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();


        for (Event possibleEvent : EventManager.getInstance().getEvents()) {

            if (possibleEvent instanceof CitadelEvent && possibleEvent.isActive()) {
                player.sendMessage(ChatColor.GOLD + "[CITADEL] " + ChatColor.YELLOW.toString() + ChatColor.UNDERLINE + possibleEvent.getName() + ChatColor.GOLD + " can be contested now.");
                return;
            }
        }

        Schedule kothEvent = ScheduleHandler.getNextEvent(true);
        FastDateFormat formatter = FastDateFormat.getInstance("EEEE, hh:mma", TimeZone.getDefault(), Locale.ENGLISH);

        player.sendMessage(ChatColor.GOLD + "[CITADEL] " + ChatColor.YELLOW + kothEvent.getName() + ChatColor.GOLD + " can be captured at " + ChatColor.BLUE + kothEvent.getFormatDay() + ChatColor.GOLD + ".");
        player.sendMessage(ChatColor.GOLD + "[CITADEL] " + ChatColor.YELLOW + "It is currently " + ChatColor.BLUE + formatter.format(System.currentTimeMillis()) + ChatColor.GOLD + ".");
        player.sendMessage(ChatColor.YELLOW + "Type '/citadel schedule' to see more upcoming KOTHs.");

    }
}
