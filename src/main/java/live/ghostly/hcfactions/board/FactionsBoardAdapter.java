package live.ghostly.hcfactions.board;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.event.Event;
import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.event.citadel.CitadelEvent;
import live.ghostly.hcfactions.event.koth.KothEvent;
import live.ghostly.hcfactions.event.sumo.SumoEvent;
import live.ghostly.hcfactions.event.sumo.SumoPlayer;
import live.ghostly.hcfactions.files.ConfigFile;
import live.ghostly.hcfactions.mode.Mode;
import live.ghostly.hcfactions.mode.ModeType;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.profile.cooldown.ProfileCooldown;
import live.ghostly.hcfactions.profile.cooldown.ProfileCooldownType;
import live.ghostly.hcfactions.profile.kit.ProfileKitCooldown;
import live.ghostly.hcfactions.profile.kit.ability.ProfileKitAbility;
import live.ghostly.hcfactions.profile.teleport.ProfileTeleportTask;
import live.ghostly.hcfactions.profile.teleport.ProfileTeleportType;
import live.ghostly.hcfactions.util.Cooldown;
import live.ghostly.hcfactions.util.PlayerUtil;
import live.ghostly.hcfactions.util.Style;
import me.joeleoli.frame.FrameAdapter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class FactionsBoardAdapter implements FrameAdapter {

    public static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");

    private FactionsPlugin main;
    private ConfigFile configFile;

    public FactionsBoardAdapter(FactionsPlugin main) {
        this.main = main;
        this.configFile = main.getScoreboardConfig();
    }

    @Override
    public String getTitle(Player player) {
        return configFile.getString("TITLE");
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> toReturn = new CopyOnWriteArrayList<>();
        Profile profile = Profile.getByPlayer(player);

        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if (sumoEvent.getPlayers().containsKey(player.getUniqueId())) {
            if (sumoEvent.isPreStart()) {
                toReturn.add("&7&m-----------------------");
                toReturn.add("&fEvent&7:&c Sumo");
                int playingSumo = sumoEvent.getByState(SumoPlayer.SumoPlayerState.WAITING).size() + sumoEvent.getByState(SumoPlayer.SumoPlayerState.FIGHTING).size() + sumoEvent.getByState(SumoPlayer.SumoPlayerState.PREPARING).size();
                toReturn.add(Style.translate("&fPlayers&7:&f &c" + playingSumo + "&7 / &c100"));
                int countdown = sumoEvent.getCountdownTask().getTimeUntilStart();
                if (countdown > 0 && countdown <= 60) {
                    toReturn.add(Style.translate("&f&l* &fStarting&7:&c " + countdown + "s"));
                }
                toReturn.add("&7&m-----------------------");
                return toReturn;
            } else if (sumoEvent.isStarted()) {
                toReturn.add("&7&m-----------------------");
                toReturn.add("&fEvent&7:&c Sumo");
                int playingSumo = sumoEvent.getByState(SumoPlayer.SumoPlayerState.WAITING).size() + sumoEvent.getByState(SumoPlayer.SumoPlayerState.FIGHTING).size() + sumoEvent.getByState(SumoPlayer.SumoPlayerState.PREPARING).size();
                toReturn.add(Style.translate("&fPlayers&7:&f &c" + playingSumo + "&7 / &c100"));

                if (sumoEvent.getPlayer(player) != null) {
                    SumoPlayer sumoPlayer = sumoEvent.getPlayer(player);
                    toReturn.add(Style.translate("&f&l* &fState&7: " + StringUtils.capitalize(sumoPlayer.getState().name().toLowerCase())));
                }

                if (sumoEvent.getByState(SumoPlayer.SumoPlayerState.FIGHTING).size() > 0) {
                    StringJoiner nameJoiner = new StringJoiner(ChatColor.RED + " vs " + ChatColor.DARK_GREEN);
                    StringJoiner pingJoiner = new StringJoiner(" | ");

                    for (UUID fighterUuid : sumoEvent.getByState(SumoPlayer.SumoPlayerState.FIGHTING)) {
                        Player fighter = Bukkit.getPlayer(fighterUuid);

                        nameJoiner.add(fighter.getName());

                        pingJoiner.add(ChatColor.GRAY + "(" + ChatColor.RED + PlayerUtil.getPing(fighter) + "ms" + ChatColor.GRAY + ")");
                    }

                    toReturn.add(ChatColor.GRAY.toString() + " ");
                    toReturn.add(ChatColor.DARK_GREEN + nameJoiner.toString());
                    toReturn.add(pingJoiner.toString());
                }
                toReturn.add("&7&m-----------------------");
                return toReturn;
            }

        }


        for (String line : configFile.getStringList("LINES")) {

            if (line.contains("%HOME%")) {
                ProfileTeleportTask teleportTask = profile.getTeleportWarmup();

                if (teleportTask != null && teleportTask.getEvent().getTeleportType() == ProfileTeleportType.HOME_TELEPORT) {
                    toReturn.add(line.replace("%HOME%", SECONDS_FORMATTER.format(((teleportTask.getEvent().getInit() + (teleportTask.getEvent().getTime() * 1000) + 50) - System.currentTimeMillis()) / 1000)));
                }

                continue;
            }

            if (line.contains("%SPAWN%")) {
                ProfileTeleportTask teleportTask = profile.getTeleportWarmup();

                if (teleportTask != null && teleportTask.getEvent().getTeleportType() == ProfileTeleportType.SPAWN) {
                    toReturn.add(line.replace("%SPAWN%", SECONDS_FORMATTER.format(((teleportTask.getEvent().getInit() + (teleportTask.getEvent().getTime() * 1000) + 50) - System.currentTimeMillis()) / 1000)));
                }

                continue;
            }

            if (line.contains("%CAMP%")) {
                ProfileTeleportTask teleportTask = profile.getTeleportWarmup();

                if (teleportTask != null && teleportTask.getEvent().getTeleportType() == ProfileTeleportType.CAMP_TELEPORT) {
                    toReturn.add(line.replace("%CAMP%", SECONDS_FORMATTER.format(((teleportTask.getEvent().getInit() + (teleportTask.getEvent().getTime() * 1000) + 50) - System.currentTimeMillis()) / 1000)));
                }

                continue;
            }

            if (line.contains("%STUCK%")) {
                ProfileTeleportTask teleportTask = profile.getTeleportWarmup();

                if (teleportTask != null && teleportTask.getEvent().getTeleportType() == ProfileTeleportType.STUCK_TELEPORT) {
                    toReturn.add(line.replace("%STUCK%", DurationFormatUtils.formatDuration((long) ((teleportTask.getEvent().getInit() + (teleportTask.getEvent().getTime() * 1000) + 500) - System.currentTimeMillis()), "mm:ss")));
                }

                continue;
            }

            if (line.contains("%KOTH%")) {
                for (Event event : EventManager.getInstance().getEvents()) {
                    if (event instanceof KothEvent && event.isActive()) {
                        toReturn.addAll(event.getScoreboardText());
                    }
                }

                continue;
            }

            if (line.contains("%CITADEL%")) {
                for (Event event : EventManager.getInstance().getEvents()) {
                    if (event instanceof CitadelEvent && event.isActive()) {
                        toReturn.addAll(event.getScoreboardText());
                    }
                }

                continue;
            }

            if (line.contains("%SOTW%")) {
                for (Mode mode : Mode.getModes()) {
                    if (mode.getModeType() == ModeType.SOTW && mode.isSOTWActive()) {
                        toReturn.addAll(mode.getScoreboardText());
                    }
                }

                continue;
            }

            if (line.contains("%SNOWBALL%")) {
                if(Cooldown.isOnCooldown("snowball", player)){
                    List<String> lines = new ArrayList<>();

                    for (String sline : main.getScoreboardConfig().getStringList("PLACE_HOLDER.SNOWBALL")) {
                        sline = sline.replace("%TIME%", String.valueOf(Cooldown.getCooldownForPlayerInt("snowball", player)));

                        lines.add(sline);
                    }
                    toReturn.addAll(lines);
                }

                continue;
            }
            if (line.contains("%EGG%")) {
                if(Cooldown.isOnCooldown("egg", player)){
                    List<String> lines = new ArrayList<>();

                    for (String sline : main.getScoreboardConfig().getStringList("PLACE_HOLDER.EGG")) {
                        sline = sline.replace("%TIME%", String.valueOf(Cooldown.getCooldownForPlayerInt("egg", player)));

                        lines.add(sline);
                    }
                    toReturn.addAll(lines);
                }

                continue;
            }
            if (line.contains("%HOOK%")) {
                if(Cooldown.isOnCooldown("grappling", player)){
                    List<String> lines = new ArrayList<>();

                    for (String sline : main.getScoreboardConfig().getStringList("PLACE_HOLDER.HOOK")) {
                        sline = sline.replace("%TIME%", String.valueOf(Cooldown.getCooldownForPlayerInt("grappling", player)));

                        lines.add(sline);
                    }
                    toReturn.addAll(lines);
                }

                continue;
            }
            if (line.contains("%ROGUE_HOOK%")) {
                ProfileKitCooldown cooldown = profile.getKitCooldownByType(ProfileKitAbility.FISHING_ROD);
                if(cooldown != null){
                    List<String> lines = new ArrayList<>();

                    for (String sline : main.getScoreboardConfig().getStringList("PLACE_HOLDER.ROGUE_HOOK")) {
                        sline = sline.replace("%TIME%", cooldown.getTimeLeft());

                        lines.add(sline);
                    }
                    toReturn.addAll(lines);
                }

                continue;
            }


            if (line.contains("%KILLS%")) {
                toReturn.add(line.replace("%KILLS%", profile.getKillCount() + ""));
                continue;
            }

            if (line.contains("%DEATHS%")) {
                toReturn.add(line.replace("%DEATHS%", profile.getDeathCount() + ""));
                continue;
            }

            if (line.contains("%KILL_STREAK%")) {
                toReturn.add(line.replace("%KILL_STREAK%", profile.getKillStreak() + ""));
                continue;
            }

            if (line.contains("%BALANCE%")) {
                toReturn.add(line.replace("%BALANCE%", profile.getBalance() + ""));
                continue;
            }

            if (line.contains("%LOGOUT%")) {
                ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.LOGOUT);

                if (cooldown != null) {
                    toReturn.add(line.replace("%LOGOUT%", cooldown.getTimeLeft()));
                }

                continue;
            }

            if (line.contains("%ARCHER_TAG%")) {
                ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.ARCHER_TAG);

                if (cooldown != null) {
                    toReturn.add(line.replace("%ARCHER_TAG%", cooldown.getTimeLeft()));
                }

                continue;
            }

            if (line.contains("%SPAWN_TAG%")) {
                ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.SPAWN_TAG);

                if (cooldown != null) {
                    toReturn.add(line.replace("%SPAWN_TAG%", cooldown.getTimeLeft()));
                }

                continue;
            }

            if (line.contains("%ENDER_PEARL%")) {
                ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.ENDER_PEARL);

                if (cooldown != null) {
                    toReturn.add(line.replace("%ENDER_PEARL%", cooldown.getTimeLeft()));
                }

                continue;
            }

            if (line.contains("%GOLDEN_APPLE%")) {
                ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.GOLDEN_APPLE);

                if (cooldown != null) {
                    toReturn.add(line.replace("%GOLDEN_APPLE%", cooldown.getTimeLeft()));
                }

                continue;
            }

            if (line.contains("%GOD_APPLE%")) {
                ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.GOD_APPLE);

                if (cooldown != null) {
                    toReturn.add(line.replace("%GOD_APPLE%", cooldown.getTimeLeft()));
                }

                continue;
            }

            if (line.contains("%PVP_PROTECTION%")) {
                if (profile.getProtection() != null && profile.isLeftSpawn()) {
                    toReturn.add(line.replace("%PVP_PROTECTION%", profile.getProtection().getTimeLeft()));
                }
                continue;
            }

            if (line.contains("%CLASS%") || line.contains("%CLASS_WARMUP%")) {
                if (profile.getKitWarmup() != null) {

                    if (profile.getKitWarmup().getKit() != null) {
                        toReturn.add(line.replace("%CLASS%", profile.getKitWarmup().getKit().getName()).replace("%CLASS_WARMUP%", profile.getKitWarmup().getTimeLeft()));
                    }
                }
                continue;
            }

            if (line.contains("%BARD%")) {
                if (profile.getEnergy() != null) {
                    for (String string : main.getScoreboardConfig().getStringList("PLACE_HOLDER.BARD")) {
                        toReturn.add(string.replace("%ENERGY%", profile.getEnergy().getFormattedString()));
                    }
                }
                continue;
            }

            toReturn.add(line);
        }

        if (toReturn.size() <= 2) {
            return null;
        }

        return toReturn;
    }
}
