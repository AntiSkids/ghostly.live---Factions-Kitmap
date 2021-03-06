package live.ghostly.hcfactions.crate.command.subcommand;


import live.ghostly.hcfactions.crate.Crate;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class CrateCreateCommand extends PluginCommand {
    @Command(name = "crate.create")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (!player.hasPermission("hcf.command." + command.getCommand().getName())) {
            player.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /crate crate <name>");
            return;
        }

        String name = StringUtils.join(args);
        Crate crate = Crate.getByName(name);

        if (crate != null) {
            player.sendMessage(ChatColor.RED + "A crate named '" + crate.getName() + "' already exists.");
            return;
        }

        crate = new Crate(name);
        player.sendMessage(ChatColor.RED + "Crate named '" + crate.getName() + "' successfully created.");
    }
}
