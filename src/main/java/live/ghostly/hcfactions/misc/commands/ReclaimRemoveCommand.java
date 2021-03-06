package live.ghostly.hcfactions.misc.commands;

import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ReclaimRemoveCommand extends PluginCommand {

    @Command(name = "rcremove", inGameOnly = false)
    public void onCommand(CommandArgs command) {

        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (!sender.hasPermission("hcf.command." + command.getCommand().getName())) {
            sender.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /rcremove <player>");
        } else if (args.length == 1) {

            Player toCheck = Bukkit.getPlayer(args[0]);

            if (!StringUtils.isNumeric(args[1])) {
                sender.sendMessage(ChatColor.RED + "Usage: /rcremove <player>");
                return;
            }


            if (toCheck == null) {
                sender.sendMessage(ChatColor.RED + "No player named '" + args[0] + "' found online.");
                return;
            }

            Profile toProfile = Profile.getByPlayer(toCheck);

            if (toProfile == null) {
                sender.sendMessage(ChatColor.RED + "No player named '" + args[0] + "' found online.");
                return;
            }


            toProfile.setReclaim(false);
            sender.sendMessage(ChatColor.YELLOW + "You have set the reclaim value to false for " + toCheck.getName());

            new BukkitRunnable() {

                @Override
                public void run() {
                    toProfile.save();
                }
            }.runTaskAsynchronously(this.main);
        }

    }

}
