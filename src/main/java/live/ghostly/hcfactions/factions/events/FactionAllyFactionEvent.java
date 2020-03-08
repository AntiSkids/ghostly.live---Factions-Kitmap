package live.ghostly.hcfactions.factions.events;

import live.ghostly.hcfactions.factions.type.PlayerFaction;
import lombok.Getter;

@Getter
public class FactionAllyFactionEvent extends FactionEvent {

    private PlayerFaction[] factions;

    public FactionAllyFactionEvent(PlayerFaction[] factions) {
        this.factions = factions;
    }

}
