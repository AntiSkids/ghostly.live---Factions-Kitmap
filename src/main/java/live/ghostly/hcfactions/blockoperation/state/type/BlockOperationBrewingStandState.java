package live.ghostly.hcfactions.blockoperation.state.type;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.blockoperation.state.BlockOperationState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.BrewingStand;

public class BlockOperationBrewingStandState implements BlockOperationState {

    private static final String NAME = "BlockOperationBrewingStandState";
    private static FactionsPlugin main = FactionsPlugin.getInstance();
    @Getter
    @Setter
    private final BrewingStand brewingStand;

    public BlockOperationBrewingStandState(BrewingStand brewingStand) {
        this.brewingStand = brewingStand;
    }

    @Override
    public Location getLocation() {
        return brewingStand.getLocation();
    }

    @Override
    public int getIncrease() {
        return main.getMainConfig().getInt("BLOCK_MODIFIER.BREWING_STAND_BREW_INCREASE");
    }

    @Override
    public String getName() {
        return NAME;
    }
}
