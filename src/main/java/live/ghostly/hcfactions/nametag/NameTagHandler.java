package live.ghostly.hcfactions.nametag;

import live.ghostly.hcfactions.util.Style;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class NameTagHandler {

	private static final String PREFIX = "nt_team_";

	private static String getTeamName(ChatColor color) {
		return PREFIX + color.ordinal();
	}

	public static void addToTeam(Player player, Player other, ChatColor color, boolean showHealth) {
		Scoreboard scoreboard = player.getScoreboard();

		if (scoreboard.equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard())) {
			scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		}

		Team team = player.getScoreboard().getTeam(getTeamName(color));

		if (team == null) {
			team = player.getScoreboard().registerNewTeam(getTeamName(color));
			team.setPrefix(color.toString());
		}

		if (!team.hasEntry(other.getName())) {
			removeFromTeams(player, other);

			team.addEntry(other.getName());

			if (showHealth) {
				Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

				if (objective == null) {
					objective = player.getScoreboard().registerNewObjective("showhealth", "health");
				}

				objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
				objective.setDisplayName(Style.RED + StringEscapeUtils.unescapeJava("\u2764"));
				objective.getScore(other.getName()).setScore((int) Math.floor(other.getHealth() / 2));
			}
		}

		player.setScoreboard(scoreboard);
	}

	public static void removeFromTeams(Player player, Player other) {
		if (player != null && other != null && !player.equals(other)) {
			Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

			if (objective != null) {
				objective.unregister();
			}

			for (ChatColor chatColor : ChatColor.values()) {
				Team team = player.getScoreboard().getTeam(getTeamName(chatColor));

				if (team != null) {
					team.removeEntry(other.getName());
				}
			}
		}
	}

	private static Team getExistingOrCreateNewTeam(String string, Scoreboard scoreboard, ChatColor prefix) {
		Team toReturn = scoreboard.getTeam(string);

		if (toReturn == null) {
			toReturn = scoreboard.registerNewTeam(string);
			toReturn.setPrefix(prefix + "");
		}

		return toReturn;
	}

	public static void removeHealthDisplay(Player player) {
		if (player == null) {
			return;
		}

		Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

		if (objective != null) {
			objective.unregister();
		}
	}

}