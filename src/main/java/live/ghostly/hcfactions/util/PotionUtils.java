package live.ghostly.hcfactions.util;

import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionUtils {

    private static final ImmutableSet<PotionEffectType> DEBUFF_TYPES = ImmutableSet.<PotionEffectType>builder().add(PotionEffectType.BLINDNESS).add(PotionEffectType.CONFUSION).add(PotionEffectType.HARM).add(PotionEffectType.HUNGER).add(PotionEffectType.POISON).add(PotionEffectType.SATURATION).add(PotionEffectType.SLOW).add(PotionEffectType.SLOW_DIGGING).add(PotionEffectType.WEAKNESS).add(PotionEffectType.WITHER).build();


    public static String getName(PotionEffectType potionEffectType) {
        if (potionEffectType.getName().equalsIgnoreCase("fire_resistance")) {
            return "Fire Resistance";
        } else if (potionEffectType.getName().equalsIgnoreCase("speed")) {
            return "Speed";
        } else if (potionEffectType.getName().equalsIgnoreCase("weakness")) {
            return "Weakness";
        } else if (potionEffectType.getName().equalsIgnoreCase("slowness")) {
            return "Slowness";
        } else {
            return "Unknown";
        }
    }

    public static boolean isDebuff(final PotionEffectType type) {
        return DEBUFF_TYPES.contains(type);
    }

    public static boolean isDebuff(final PotionEffect potionEffect) {
        return isDebuff(potionEffect.getType());
    }

    public static boolean isDebuff(final ThrownPotion thrownPotion) {
        for (final PotionEffect effect : thrownPotion.getEffects()) {
            if (isDebuff(effect)) {
                return true;
            }
        }
        return false;
    }

}
