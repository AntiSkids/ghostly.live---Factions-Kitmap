package live.ghostly.hcfactions.factions.type;

import live.ghostly.hcfactions.factions.Faction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class SystemFaction extends Faction {

    private ChatColor color = ChatColor.WHITE;
    private boolean deathban = true;
    private boolean pvpWall = false;

    public SystemFaction(String name, UUID uuid) {
        super(name, uuid);
    }

    public static Set<SystemFaction> getSystemFactions() {
        Set<SystemFaction> toReturn = new HashSet<>();

        for (Faction faction : getFactions()) {
            if (faction instanceof SystemFaction) {
                toReturn.add((SystemFaction) faction);
            }
        }

        return toReturn;
    }

    public static SystemFaction getByName(String name) {
        for (SystemFaction systemFaction : getSystemFactions()) {
            if (systemFaction.getName().replace(" ", "").equalsIgnoreCase(name)) {
                return systemFaction;
            }
        }

        return null;
    }

}
