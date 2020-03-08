package live.ghostly.hcfactions.util;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public final class PlayerUtil {

    private PlayerUtil() {
    }

    public static void setFirstSlotOfType(Player player, Material type, ItemStack itemStack) {
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack itemStack1 = player.getInventory().getContents()[i];
            if (itemStack1 == null || itemStack1.getType() == type || itemStack1.getType() == Material.AIR) {
                player.getInventory().setItem(i, itemStack);
                break;
            }
        }
    }

    public static String getColorPrefix(String prefix) {
        if (prefix.isEmpty()) {
            return "";
        }
        char code = 'f';
        char magic = 'f';
        for (String string : prefix.split("&")) {
            if ((!string.isEmpty()) && (ChatColor.getByChar(string.toCharArray()[0]) != null)) {
                if (!isMagic(string.toCharArray()[0])) {
                    code = string.toCharArray()[0];
                } else {
                    magic = string.toCharArray()[0];
                }
            }
        }
        ChatColor color = ChatColor.getByChar(code);
        if (magic == 'f') {
            return color.toString();
        }
        ChatColor magicColor = ChatColor.getByChar(magic);
        return color.toString() + magicColor.toString();
    }

    private static boolean isMagic(char string) {
        return string == 'o' || string == 'l' || string == 'k' || string == 'n' || string == 'm';
    }

    public static int getPing(Player player) {

        return ((CraftPlayer) player).getHandle().ping;
    }

    public static void denyMovement(Player player) {
        player.setWalkSpeed(0.0F);
        player.setFlySpeed(0.0F);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
    }

    public static void allowMovement(Player player) {
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.1F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
    }

    public static void clearPlayer(Player player) {
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setSaturation(12.8F);
        player.setMaximumNoDamageTicks(20);
        player.setFireTicks(0);
        player.setFallDistance(0.0F);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setWalkSpeed(0.2F);
        player.getInventory().setHeldItemSlot(0);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.closeInventory();
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        //((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
        player.updateInventory();
    }

    public static void sendMessage(String message, Player... players) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public static void sendMessage(String message, Set<Player> players) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public static void sendFirework(FireworkEffect effect, Location location) {
        Firework f = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(effect);
        f.setFireworkMeta(fm);

        try {
            Class<?> entityFireworkClass = getClass("net.minecraft.server.", "EntityFireworks");
            Class<?> craftFireworkClass = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
            Object firework = craftFireworkClass.cast(f);
            Method handle = firework.getClass().getMethod("getHandle");
            Object entityFirework = handle.invoke(firework);
            Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
            Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
            ticksFlown.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Class<?> getClass(String prefix, String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        Class<?> nmsClass = Class.forName(name);
        return nmsClass;
    }
}
