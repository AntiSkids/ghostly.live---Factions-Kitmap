package live.ghostly.hcfactions.profile.protection.life.command.subcommand;


import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.profile.protection.life.ProfileProtectionLifeType;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileProtectionLifeSetCommand extends PluginCommand {
    @Command(name = "lives.set", aliases = {"lifes.set"}, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (!sender.hasPermission("hcf.command." + command.getCommand().getName())) {
            sender.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /lives set <target> <type> <amount>");
            return;
        }

        Profile profile;
        Player player = Bukkit.getPlayer(args[0]);
        if (player != null) {
            profile = Profile.getByPlayer(player);
        } else {
            profile = Profile.getByName(args[0]);
        }

        if (profile == null) {
            sender.sendMessage(ChatColor.RED + "No player with name '" + args[0] + "' found.");
            return;
        }

        ProfileProtectionLifeType type;
        try {
            type = ProfileProtectionLifeType.valueOf(args[1].toUpperCase());
        } catch (Exception exception) {
            sender.sendMessage(ChatColor.RED + "Invalid life type.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (Exception exception) {
            sender.sendMessage(ChatColor.RED + "Invalid amount.");
            return;
        }

        if (amount < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid amount.");
            return;
        }

        profile.getLives().put(type, amount);

        sender.sendMessage(ChatColor.YELLOW + "You have modified " + ChatColor.RED + profile.getName() + ChatColor.YELLOW + "'s " + type.name().toLowerCase() + " lives to " + ChatColor.RED + profile.getLives().get(type) + ChatColor.YELLOW + ".");

        if (Bukkit.getPlayer(profile.getUuid()) == null) {
            Profile.getProfilesMap().remove(profile.getUuid());
        }
    }
}
