package live.ghostly.hcfactions.economysign;

import live.ghostly.hcfactions.crowbar.Crowbar;
import live.ghostly.hcfactions.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

public class EconomySign {

    @Getter
    private final Sign sign;
    @Getter
    private final EconomySignType type;
    @Getter
    private final ItemStack itemStack;
    @Getter
    private final int amount;
    @Getter
    private final int price;

    public EconomySign(Sign sign, EconomySignType type, ItemStack itemStack, int amount, int price) {
        this.sign = sign;
        this.type = type;
        this.itemStack = itemStack;
        this.amount = amount;
        this.price = price;
    }

    public static EconomySign getByBlock(Block block) {
        BlockState state = block.getState();
        if (state instanceof Sign) {
            Sign sign = (Sign) state;
            String[] lines = sign.getLines();

            EconomySignType type = null;
            for (EconomySignType possibleType : EconomySignType.values()) {
                if (possibleType.getSignText().get(0).equals(lines[0])) {
                    type = possibleType;
                }
            }

            if (type == null) {
                return null;
            }

            String materialName = lines[1];
            ItemStack itemStack;

            if (materialName.equalsIgnoreCase("Crowbar")) {
                itemStack = Crowbar.getNewCrowbar().getItemStack();
            } else if (materialName.equalsIgnoreCase("Portal Frame")) {
                itemStack = new ItemStack(Material.ENDER_PORTAL_FRAME);
            } else if (materialName.equalsIgnoreCase("Cow Egg")) {
                itemStack = new ItemBuilder(Material.MONSTER_EGG).durability(92).build();
            } else if (materialName.equalsIgnoreCase("Nether Wart")) {
                itemStack = new ItemStack(Material.NETHER_STALK);
            } else if (materialName.equalsIgnoreCase("Fresh Potato")) {
                itemStack = new ItemStack(Material.POTATO_ITEM);
            } else if (materialName.equalsIgnoreCase("Fresh Carrot")) {
                itemStack = new ItemStack(Material.CARROT_ITEM);
            } else if (materialName.equalsIgnoreCase("Dye")) {
                itemStack = new ItemStack(Material.INK_SACK);
            } else if (materialName.equalsIgnoreCase("Fermented Eye")) {
                itemStack = new ItemStack(Material.FERMENTED_SPIDER_EYE);
            } else if (materialName.equalsIgnoreCase("Fire Resistance")) {
                itemStack = new ItemBuilder(Material.POTION).durability(8259).build();
            } else if (materialName.equalsIgnoreCase("Invisibility")) {
                itemStack = new ItemBuilder(Material.POTION).durability(8270).build();
            } else if (materialName.equalsIgnoreCase("Strength I")) {
                itemStack = new ItemBuilder(Material.POTION).durability(8201).build();
            } else if (materialName.equalsIgnoreCase("Poison Splash")) {
                itemStack = new ItemBuilder(Material.POTION).durability(16388).build();
            } else if (materialName.equalsIgnoreCase("Slowness Splash")) {
                itemStack = new ItemBuilder(Material.POTION).durability(16394).build();
            } else if (materialName.equalsIgnoreCase("Weakness Splash")) {
                itemStack = new ItemBuilder(Material.POTION).durability(16392).build();
            } else if (materialName.equalsIgnoreCase("Gold Apple")) {
                itemStack = new ItemBuilder(Material.GOLDEN_APPLE).build();
            } else if (materialName.equalsIgnoreCase("God Apple")) {
                itemStack = new ItemBuilder(Material.GOLDEN_APPLE).durability(1).build();
            } else {
                try {
                    itemStack = new ItemStack(Material.valueOf(materialName.replace(" ", "_").toUpperCase()));
                } catch (Exception ex) {
                    return null;
                }
            }

            int amount;
            try {
                amount = Integer.parseInt(lines[2].replaceAll("[^0-9]", ""));
            } catch (Exception ex) {
                return null;
            }

            int price;
            try {
                price = Integer.parseInt(lines[3].replaceAll("[^0-9]", ""));
            } catch (Exception ex) {
                return null;
            }


            return new EconomySign(sign, type, itemStack, amount, price);
        }

        return null;
    }

}
