package live.ghostly.hcfactions.factions.events.player;

import live.ghostly.hcfactions.factions.events.FactionEvent;
import live.ghostly.hcfactions.factions.type.PlayerFaction;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerJoinFactionEvent extends FactionEvent {

    private PlayerFaction faction;
    private Player player;

    public PlayerJoinFactionEvent(Player player, PlayerFaction faction) {
        this.player = player;
        this.faction = faction;
    }

}
