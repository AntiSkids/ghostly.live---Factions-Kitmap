package live.ghostly.hcfactions.profile.deathban;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.util.DateUtil;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class ProfileDeathban {

    public static final String KICK_MESSAGE = StringUtils.join(FactionsPlugin.getInstance().getLanguageConfig().getStringList("DEATHBAN.KICK_MESSAGE"), "\n");
    public static final String LIFE_USE_MESSAGE = StringUtils.join(FactionsPlugin.getInstance().getLanguageConfig().getStringList("DEATHBAN.USE_LIFE"), "\n");


    @Getter
    private final long createdAt;
    @Getter
    private final long duration;
    @Getter
    private boolean ip;


    public ProfileDeathban(long createdAt, long duration) {
        this.createdAt = createdAt;
        this.duration = duration;
        this.ip = true;
    }

    public ProfileDeathban(long duration) {
        this(System.currentTimeMillis(), duration * 1000);
    }

    public static int getDuration(Player player) {
        int duration = 0;

        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String perm = info.getPermission();

            if (perm.equalsIgnoreCase("deathban.bypass")) {
                return 0;
            }

            if (perm.startsWith("deathban.")) {
                int tempDuration = 0;

                try {
                    tempDuration = Integer.parseInt(perm.replace("deathban.", "").replace(" ", ""));
                } catch (NumberFormatException ignored) {
                }

                if (duration > 0 && tempDuration > duration) {
                    continue;
                }

                duration = tempDuration;
            }
        }

        return duration;
    }

    public String getTimeLeft() {
        return DateUtil.readableTime((createdAt + duration) - System.currentTimeMillis()).trim();
    }

}
