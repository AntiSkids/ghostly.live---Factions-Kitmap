package live.ghostly.hcfactions.misc.commands;

import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ReclaimCommand extends PluginCommand {

    @Command(name = "reclaim", aliases = {"rc", "claim"}, inGameOnly = true)
    public void onCommand(CommandArgs command) {
        this.runCommands(command.getPlayer());
    }

    private void runCommands(Player player) {

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile == null) {
            return;
        }

        if (profile.isReclaim()) {
            player.sendMessage(this.main.getLanguageConfig().getString("RECLAIM.NONE"));
            return;
        }/*

        String rankName = NucleusPlayer.getByUuid(player.getUniqueId()) == null ? "DEFAULT" : Nucleus.getInstance().getChat().getPrimaryGroup(player).toUpperCase();

        if(!this.main.getConfig().contains("RECLAIM." + rankName)) {
            player.sendMessage(this.main.getLanguageConfig().getString("RECLAIM.NONE"));
            return;
        }

        for(String key : this.main.getConfig().getStringList("RECLAIM." + rankName)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), key.replace("%PLAYER%", player.getName()));
        }

        Bukkit.broadcastMessage(this.main.getLanguageConfig().getString("RECLAIM.SUCCESS").replace("%PLAYER%", player.getName()));

        profile.setReclaim(true);*/

        new BukkitRunnable() {

            @Override
            public void run() {
                profile.save();
            }
        }.runTaskAsynchronously(this.main);

    }

}
