package live.ghostly.hcfactions.event.sumo.command;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.event.sumo.SumoEvent;
import live.ghostly.hcfactions.files.ConfigFile;
import live.ghostly.hcfactions.util.LocationSerialization;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.Style;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SumoSetSpawn extends PluginCommand {

    private FactionsPlugin plugin = FactionsPlugin.getInstance();
    private ConfigFile config = plugin.getMainConfig();

    @Command(name = "sumo.setspawn")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (!player.hasPermission("sumo.setspawn")) {
            player.sendMessage(Style.translate("&cNo permissions"));
            return;
        }

        if (command.getArgs().length == 0) {
            command.getSender().sendMessage(Style.translate("&eUse&7:"));
            command.getSender().sendMessage(Style.translate("&a/sumo setspawn &7lobby"));
            command.getSender().sendMessage(Style.translate("&a/sumo setspawn &7first"));
            command.getSender().sendMessage(Style.translate("&a/sumo setspawn &7second"));
            return;
        }

        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        Location location = player.getLocation();
        location.setX(location.getBlockX() + 0.5);
        location.setY(location.getBlockY() + 3.0);
        location.setZ(location.getBlockZ() + 0.5);

        if (command.getArgs()[0].equals("lobby")) {
            sumoEvent.setWaitLocation(location);
            player.sendMessage(Style.translate("&aSuccessfully set lobby for sumo."));
            this.main.getConfig().set("waitLocation", LocationSerialization.serializeLocation(location));
            this.main.saveConfig();
        } else if (command.getArgs()[0].equals("first")) {
            sumoEvent.setFirstSpawnLocation(location);
            player.sendMessage(Style.translate("&aSuccessfully set position first for sumo."));
            this.main.getConfig().set("firstSpawnLocation", LocationSerialization.serializeLocation(location));
            this.main.saveConfig();
        } else if (command.getArgs()[0].equals("second")) {
            sumoEvent.setSecondSpawnLocation(location);
            player.sendMessage(Style.translate("&aSuccessfully set position second for sumo."));
            this.main.getConfig().set("secondSpawnLocation", LocationSerialization.serializeLocation(location));
            this.main.saveConfig();
        } else {
            command.getSender().sendMessage(Style.translate("&eUse&7:"));
            command.getSender().sendMessage(Style.translate("&a/sumo setspawn &7lobby"));
            command.getSender().sendMessage(Style.translate("&a/sumo setspawn &7first"));
            command.getSender().sendMessage(Style.translate("&a/sumo setspawn &7second"));
        }
    }
}
