package live.ghostly.hcfactions.factions.events.player;

import live.ghostly.hcfactions.factions.Faction;
import live.ghostly.hcfactions.factions.events.FactionEvent;
import live.ghostly.hcfactions.profile.teleport.ProfileTeleportType;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerCancelFactionTeleportEvent extends FactionEvent {

    private Faction faction;
    private Player player;
    private ProfileTeleportType teleportType;

    public PlayerCancelFactionTeleportEvent(Player player, Faction faction, ProfileTeleportType teleportType) {
        this.player = player;
        this.faction = faction;
        this.teleportType = teleportType;
    }


}
