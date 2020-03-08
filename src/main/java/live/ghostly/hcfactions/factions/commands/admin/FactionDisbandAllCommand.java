package live.ghostly.hcfactions.factions.commands.admin;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.factions.Faction;
import live.ghostly.hcfactions.factions.claims.Claim;
import live.ghostly.hcfactions.factions.commands.FactionCommand;
import live.ghostly.hcfactions.factions.type.PlayerFaction;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.HashSet;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;

public class FactionDisbandAllCommand extends FactionCommand {

    @Command(name = "f.disbandall", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();

        if (!sender.hasPermission("hcf.command." + command.getCommand().getName())) {
            sender.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (command.getArgs().length == 0 && sender instanceof ConsoleCommandSender) {
            for (Faction faction : Faction.getFactions()) {
                if (faction instanceof PlayerFaction) {
                    Set<Claim> claims = new HashSet<>(faction.getClaims());

                    for (Claim claim : claims) {
                        claim.remove();
                    }

                    Bukkit.getScheduler().runTaskAsynchronously(FactionsPlugin.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            main.getFactionsDatabase().getDatabase().getCollection("playerFactions").deleteOne(eq("uuid", faction.getUuid().toString()));
                        }
                    });

                    Faction.getFactions().remove(faction);
                }
            }

            sender.sendMessage(langConfig.getString("ADMIN.DISBAND_ALL"));
        }
    }
}
