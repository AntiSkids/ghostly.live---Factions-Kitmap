package live.ghostly.hcfactions.misc.commands;

import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.PluginCommand;
import live.ghostly.hcfactions.util.Style;
import live.ghostly.hcfactions.util.command.Command;
import live.ghostly.hcfactions.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class StatsCommand extends PluginCommand {


    @Command(name = "stats", inGameOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        Profile profile = Profile.getByPlayer(player);

        if (!player.hasPermission("hcf.command.stats")) {
            player.sendMessage(PluginCommand.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            player.sendMessage(Style.translate("&7&m------------------------------------------------"));
            player.sendMessage(Style.translate("&2&lYour Stats"));
            player.sendMessage(Style.translate(""));
            player.sendMessage(Style.translate("&a&lPvp Stats"));
            player.sendMessage(Style.translate(" &aKills&7: " + profile.getKillCount()));
            player.sendMessage(Style.translate(" &aDeaths&7: " + profile.getDeathCount()));
            player.sendMessage(Style.translate(""));
            player.sendMessage(Style.translate("&a&lOres"));
            player.sendMessage(Style.translate(" &bDiamond Mined&7: " + player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE)));
            player.sendMessage(Style.translate(" &aEmerald Mined&7: " + player.getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE)));
            player.sendMessage(Style.translate(" &eGold Mined&7: " + player.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE)));
            player.sendMessage(Style.translate(" &7Iron Mined&7: " + player.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE)));
            player.sendMessage(Style.translate(" &9Coal Mined&7: " + player.getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE)));
            player.sendMessage(Style.translate(" &9Lapis Mined&7: " + player.getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE)));
            player.sendMessage(Style.translate(" &cRedStone Mined&7: " + player.getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE)));
            player.sendMessage(Style.translate(""));
            player.sendMessage(Style.translate("&7&m------------------------------------------------"));
            return;
        }
        Player other = Bukkit.getPlayer(args[0]);
        if (other == null) {
            player.sendMessage(ChatColor.RED + "No player named '" + args[0] + "' found online.");
            return;
        }
        Profile profileother = Profile.getByUuid(other.getUniqueId());

        if (args.length == 1) {
            player.sendMessage(Style.translate("&7&m------------------------------------------------"));
            player.sendMessage(Style.translate("&2&l" + profileother.getName() + " Stats"));
            player.sendMessage(Style.translate(""));
            player.sendMessage(Style.translate("&a&lPvp Stats"));
            player.sendMessage(Style.translate(" &aKills&7: " + profileother.getKillCount()));
            player.sendMessage(Style.translate(" &aDeaths&7: " + profileother.getDeathCount()));
            player.sendMessage(Style.translate(""));
            player.sendMessage(Style.translate("&a&lOres"));
            player.sendMessage(Style.translate(" &bDiamond Mined&7: " + other.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE)));
            player.sendMessage(Style.translate(" &aEmerald Mined&7: " + other.getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE)));
            player.sendMessage(Style.translate(" &eGold Mined&7: " + other.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE)));
            player.sendMessage(Style.translate(" &7Iron Mined&7: " + other.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE)));
            player.sendMessage(Style.translate(" &9Coal Mined&7: " + other.getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE)));
            player.sendMessage(Style.translate(" &9Lapis Mined&7: " + other.getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE)));
            player.sendMessage(Style.translate(" &cRedStone Mined&7: " + other.getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE)));
            player.sendMessage(Style.translate(""));
            player.sendMessage(Style.translate("&7&m------------------------------------------------"));
        }
    }

}
