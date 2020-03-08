package live.ghostly.hcfactions.profile.protection;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.factions.claims.Claim;
import live.ghostly.hcfactions.factions.type.SystemFaction;
import live.ghostly.hcfactions.mode.Mode;
import live.ghostly.hcfactions.mode.ModeType;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.DateUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

public class ProfileProtection {

    public static final int DEFAULT_DURATION = FactionsPlugin.getInstance().getMainConfig().getInt("PVP_PROTECTION.DURATION");
    private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");
    @Getter
    private long createdAt;
    @Getter
    private long duration;
    @Getter
    private boolean paused;

    public ProfileProtection(long duration) {
        this.createdAt = System.currentTimeMillis();
        this.duration = (duration * 1000) + 999;
    }

    public static void run(FactionsPlugin main) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Profile profile : Profile.getProfiles()) {
                    ProfileProtection protection = profile.getProtection();
                    if (protection != null) {

                        for (Mode mode : Mode.getModes()) {
                            if(mode.getModeType() == ModeType.EOTW){
                                profile.setProtection(null);
                                break;
                            }
                        }

                        if(profile == null){
                            continue;
                        }

                        if (protection.getDuration() <= 0) {
                            profile.setProtection(null);
                            continue;
                        }

                        Player player = Bukkit.getPlayer(profile.getUuid());

                        if (player != null) {
                            Claim claim = Claim.getProminentClaimAt(player.getLocation());

                            if (protection.isPaused()) {
                                if (claim != null && claim.getFaction() instanceof SystemFaction && !((SystemFaction) claim.getFaction()).isDeathban()) {
                                    continue;
                                }

                                profile.setLeftSpawn(true);
                                protection.unpause();
                            } else {
                                if (claim != null && claim.getFaction() instanceof SystemFaction && !((SystemFaction) claim.getFaction()).isDeathban()) {
                                    protection.pause();
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(main, 2L, 2L);
    }

    public String getTimeLeft() {

        long time = getDurationLeft() * 1000;

        if (time >= 3600000) {
            return DateUtil.formatTime(time);
        } else if (time >= 60000) {
            return DateUtil.formatTime(time);
        } else {
            return SECONDS_FORMATTER.format(((time) / 1000.0f)) + "s";
        }
    }

    public int getDurationLeft() {
        if (paused) {
            return (int) duration / 1000;
        }
        return (int) ((createdAt + duration) - System.currentTimeMillis()) / 1000;
    }

    public void pause() {
        duration = getDurationLeft() * 1000;
        paused = true;
    }

    public void unpause() {
        paused = false;
        createdAt = System.currentTimeMillis() + 999;
    }

}
