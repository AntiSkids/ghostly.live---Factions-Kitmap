package live.ghostly.hcfactions.event.citadel.command;

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

public class CitadelScheduleCommand extends PluginCommand {

    @Command(name = "citadel.schedule", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        FastDateFormat formatter = FastDateFormat.getInstance("EEEE, hh:mma", TimeZone.getDefault(), Locale.ENGLISH);

        for (Schedule schedule : ScheduleHandler.citadelSchedules) {
            player.sendMessage(ChatColor.GOLD + "[CITADEL] " + ChatColor.YELLOW + schedule.getName() + ChatColor.GOLD + " can be captured at " + ChatColor.BLUE + schedule.getFormatDay() + ChatColor.GOLD + ".");
        }

        player.sendMessage(ChatColor.GOLD + "[CITADEL] " + ChatColor.YELLOW + "It is currently " + ChatColor.BLUE + formatter.format(System.currentTimeMillis()) + ChatColor.GOLD + ".");

    }
}
