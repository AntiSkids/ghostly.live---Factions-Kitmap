package live.ghostly.hcfactions.misc.commands;

import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetGKitCommand extends PluginCommand {

    @Command(name = "setgkit", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (!sender.hasPermission("hcf.command." + command.getCommand().getName())) {
            sender.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /setgkit <player> <name> <true/false>");
        } else if (args.length == 3) {

            Player toCheck = Bukkit.getPlayer(args[0]);
            String kitName = args[1].toLowerCase();
            boolean value = Boolean.parseBoolean(args[2]);

            if (!isValidKit(kitName)) {
                sender.sendMessage(ChatColor.RED + "No kit named '" + kitName + "' was found.");
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

            if (value) {

                if (toProfile.getBoughtKits().contains(kitName)) {
                    sender.sendMessage(ChatColor.RED + "This player already has that kit.");
                    return;
                }

                toProfile.getBoughtKits().add(kitName);

                sender.sendMessage(ChatColor.GREEN + "You gave the kit " + kitName.toUpperCase() + " to " + toCheck.getName() + ".");
            } else {

                if (!toProfile.getBoughtKits().contains(kitName)) {
                    sender.sendMessage(ChatColor.RED + "This player doesn't have that kit.");
                    return;
                }

                toProfile.getBoughtKits().remove(kitName);

                sender.sendMessage(ChatColor.GREEN + "You removed the kit " + kitName.toUpperCase() + " from " + toCheck.getName() + ".");

            }

        }

    }

    private boolean isValidKit(String kitName) {
        return kitName.equalsIgnoreCase("god") || kitName.equalsIgnoreCase("starter") || kitName.equalsIgnoreCase("legendary") || kitName.equalsIgnoreCase("diamond") || kitName.equalsIgnoreCase("bard") || kitName.equalsIgnoreCase("miner") || kitName.equalsIgnoreCase("archer") || kitName.equalsIgnoreCase("rogue");
    }

}
