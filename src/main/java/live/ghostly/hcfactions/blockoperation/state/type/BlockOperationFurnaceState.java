package live.ghostly.hcfactions.blockoperation.state.type;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.blockoperation.state.BlockOperationState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Furnace;

public class BlockOperationFurnaceState implements BlockOperationState {

    private static final String NAME = "BlockOperationFurnaceState";
    private static FactionsPlugin main = FactionsPlugin.getInstance();
    @Getter
    @Setter
    private final Furnace furnace;

    public BlockOperationFurnaceState(Furnace furnace) {
        this.furnace = furnace;
    }

    @Override
    public Location getLocation() {
        return furnace.getLocation();
    }

    @Override
    public int getIncrease() {
        return main.getMainConfig().getInt("BLOCK_MODIFIER.FURNACE_COOK_INCREASE");
    }

    @Override
    public String getName() {
        return NAME;
    }
}
