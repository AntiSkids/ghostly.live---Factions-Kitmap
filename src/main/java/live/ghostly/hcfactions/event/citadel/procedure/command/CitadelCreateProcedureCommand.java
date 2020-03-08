package live.ghostly.hcfactions.event.citadel.procedure.command;


import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.event.citadel.procedure.CitadelCreateProcedure;
import live.ghostly.hcfactions.event.citadel.procedure.CitadelCreateProcedureStage;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class CitadelCreateProcedureCommand extends PluginCommand {

    @Command(name = "citadel.create", aliases = {"citadel.new", "createcitadel", "newcitadel"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        String[] args = command.getArgs();

        if (!player.hasPermission("hcf.command." + command.getCommand().getName())) {
            player.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (profile.getKothCreateProcedure() != null) {
            player.sendMessage(" ");
            player.sendMessage(ChatColor.RED + "You're already in the process of creating a KoTH.");
            player.sendMessage(ChatColor.RED + "Type 'cancel' in chat to stop the procedure.");
            player.sendMessage(" ");
            return;
        }

        if (args.length == 0) {
            profile.setCitadelCreateProcedure(new CitadelCreateProcedure().stage(CitadelCreateProcedureStage.NAME_SELECTION));
            player.sendMessage(" ");
            player.sendMessage(ChatColor.YELLOW + "Please type a name for this Citadel in chat.");
            player.sendMessage(" ");
        } else {

            if (EventManager.getInstance().getByName(StringUtils.join(args)) != null) {
                player.sendMessage(" ");
                player.sendMessage(ChatColor.RED + "An event with that name already exists.");
                player.sendMessage(" ");
                return;
            }

            profile.setCitadelCreateProcedure(new CitadelCreateProcedure().stage(CitadelCreateProcedureStage.ZONE_SELECTION).name(StringUtils.join(args)));
            player.sendMessage(" ");
            player.sendMessage(ChatColor.YELLOW + "You have received the zone wand.");
            player.sendMessage(" ");
            player.getInventory().removeItem(CitadelCreateProcedure.getWand());
            player.getInventory().addItem(CitadelCreateProcedure.getWand());
        }

    }
}
