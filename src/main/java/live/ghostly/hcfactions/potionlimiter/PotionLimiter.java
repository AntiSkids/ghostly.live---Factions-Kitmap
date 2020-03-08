package live.ghostly.hcfactions.potionlimiter;

import live.ghostly.hcfactions.FactionsPlugin;

public class PotionLimiter {

    private static final PotionLimiter instance = new PotionLimiter();
    private static FactionsPlugin main = FactionsPlugin.getInstance();

    public static PotionLimiter getInstance() {
        return instance;
    }

    public boolean isBlocked(int data) {
        return main.getMainConfig().getStringList("BLOCKED_POTIONS").contains(data + "");
    }

}
