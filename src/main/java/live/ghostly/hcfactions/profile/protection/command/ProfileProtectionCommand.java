package live.ghostly.hcfactions.profile.protection.command;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.profile.protection.command.subcommand.ProfileProtectionEnableCommand;
import live.ghostly.hcfactions.profile.protection.command.subcommand.ProfileProtectionLivesCommand;
import live.ghostly.hcfactions.profile.protection.command.subcommand.ProfileProtectionReviveCommand;
import live.ghostly.hcfactions.profile.protection.command.subcommand.ProfileProtectionTimeCommand;
import live.ghostly.hcfactions.profile.protection.life.command.ProfileProtectionLifeCommand;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;

import java.util.List;

public class ProfileProtectionCommand extends PluginCommand {

    public static final List<String> HELP_MESSAGE = FactionsPlugin.getInstance().getLanguageConfig().getStringList("PVP_PROTECTION.COMMAND.HELP");

    public ProfileProtectionCommand() {
        new ProfileProtectionEnableCommand();
        new ProfileProtectionLivesCommand();
        new ProfileProtectionReviveCommand();
        new ProfileProtectionTimeCommand();
        new ProfileProtectionLifeCommand();
    }

    public static String[] getHelp() {
        return HELP_MESSAGE.toArray(new String[HELP_MESSAGE.size()]);
    }

    @Command(name = "pvp", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        command.getSender().sendMessage(getHelp());
    }
}
