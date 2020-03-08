package live.ghostly.hcfactions.util;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.files.ConfigFile;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.ChatColor;

public abstract class PluginCommand {

    public static String NO_PERMISSION = ChatColor.RED + "No permission.";
    public FactionsPlugin main = FactionsPlugin.getInstance();
    public ConfigFile configFile = main.getMainConfig();
    public ConfigFile langFile = main.getLanguageConfig();
    public ConfigFile scoreboardFile = main.getScoreboardConfig();

    public PluginCommand() {
        main.getFramework().registerCommands(this);
    }

    public abstract void onCommand(CommandArgs command);

}
