package live.ghostly.hcfactions.event.koth.command;


import live.ghostly.hcfactions.event.Event;
import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.event.koth.KothEvent;
import live.ghostly.hcfactions.util.DateUtil;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class KothStartCommand extends PluginCommand {

    private static final long DEFAULT_DURATION = 900000;

    @Command(name = "koth.start")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (!sender.hasPermission("hcf.command." + command.getCommand().getName())) {
            sender.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/koth start <koth> [<duration>]");
            return;
        }

        Event event = EventManager.getInstance().getByName(args[0]);

        if (event == null || (!(event instanceof KothEvent))) {
            sender.sendMessage(ChatColor.RED + "Please specify a valid KoTH.");
            return;
        }

        long capTime = DEFAULT_DURATION;

        if (args.length > 1) {
            try {
                capTime = System.currentTimeMillis() - DateUtil.parseDateDiff(args[1], false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        KothEvent koth = (KothEvent) event;

        if (koth.isActive()) {
            sender.sendMessage(ChatColor.RED + "KoTH " + koth.getName() + " is already active!");
            return;
        }

        koth.start(capTime);


    }
}
