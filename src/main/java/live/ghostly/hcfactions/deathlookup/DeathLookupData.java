package live.ghostly.hcfactions.deathlookup;

import live.ghostly.hcfactions.profile.fight.ProfileFight;
import lombok.Getter;
import lombok.Setter;

public class DeathLookupData {

    @Getter
    @Setter
    private ProfileFight fight;
    @Getter
    @Setter
    private int page;
    @Getter
    @Setter
    private int index;

}
