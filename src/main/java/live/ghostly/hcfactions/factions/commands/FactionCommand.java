package live.ghostly.hcfactions.factions.commands;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.files.ConfigFile;

public class FactionCommand {

    public FactionsPlugin main = FactionsPlugin.getInstance();
    public ConfigFile langConfig = main.getLanguageConfig();
    public ConfigFile mainConfig = main.getMainConfig();

    public FactionCommand() {
        main.getFramework().registerCommands(this);
    }

}
