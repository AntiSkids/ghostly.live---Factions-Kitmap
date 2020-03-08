package live.ghostly.hcfactions.enchantmentlimiter;

import live.ghostly.hcfactions.FactionsPlugin;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentLimiter {

    private static final EnchantmentLimiter instance = new EnchantmentLimiter();
    private static FactionsPlugin main = FactionsPlugin.getInstance();

    public static EnchantmentLimiter getInstance() {
        return instance;
    }

    public int getEnchantmentLimit(Enchantment enchantment) {
        if (main.getMainConfig().getConfiguration().contains("ENCHANTMENT_LIMITER." + enchantment.getName())) {
            return main.getMainConfig().getInt("ENCHANTMENT_LIMITER." + enchantment.getName());
        }

        return enchantment.getMaxLevel();
    }

}
