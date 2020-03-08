package live.ghostly.hcfactions.factions.events;

import live.ghostly.hcfactions.factions.type.PlayerFaction;
import lombok.Getter;

@Getter
public class FactionEnemyFactionEvent extends FactionEvent {

    private PlayerFaction[] factions;

    public FactionEnemyFactionEvent(PlayerFaction[] factions) {
        this.factions = factions;
    }

}
