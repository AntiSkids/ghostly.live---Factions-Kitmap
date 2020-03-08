package live.ghostly.hcfactions.profile.cooldown;

import live.ghostly.hcfactions.FactionsPlugin;

public enum ProfileCooldownType {
    ENDER_PEARL,
    SPAWN_TAG,
    ARCHER_TAG,
    GOLDEN_APPLE,
    GOD_APPLE,
    GKIT,
    LOGOUT;

    private static FactionsPlugin main = FactionsPlugin.getInstance();

    public int getDuration() {
        return main.getMainConfig().getInt("COOLDOWN." + name());
    }

    public String getMessage() {
        return main.getLanguageConfig().getString("COOLDOWN." + name());
    }

}
