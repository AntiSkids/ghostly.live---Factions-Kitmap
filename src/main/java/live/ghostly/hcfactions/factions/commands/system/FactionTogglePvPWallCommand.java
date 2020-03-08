package live.ghostly.hcfactions.factions.commands.system;


import live.ghostly.hcfactions.factions.commands.FactionCommand;
import live.ghostly.hcfactions.factions.type.SystemFaction;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.command.CommandSender;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionTogglePvPWallCommand extends FactionCommand {
    @Command(name = "f.togglepvpwall", aliases = {"faction.togglepvpwall", "factions.togglepvpwall", "f.pvpwall", "faction.pvpwall", "factions.pvpwall"}, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getPlayer();

        if (!sender.hasPermission("hcf.command." + command.getCommand().getName())) {
            sender.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(langConfig.getString("TOO_FEW_ARGS.TOGGLE_DEATHBAN"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(command.getArgs()[i]).append(" ");
        }

        String name = sb.toString().trim();

        SystemFaction systemFaction = SystemFaction.getByName(name);

        if (systemFaction == null) {
            sender.sendMessage(langConfig.getString("ERROR.SYSTEM_FACTION_NOT_FOUND").replace("%NAME%", name));
            return;
        }

        systemFaction.setPvpWall(!systemFaction.isPvpWall());
        sender.sendMessage(langConfig.getString("SYSTEM_FACTION.TOGGLED_PVPWALL").replace("%FACTION%", systemFaction.getName()).replace("%BOOLEAN%", systemFaction.isPvpWall() + ""));
    }
}
