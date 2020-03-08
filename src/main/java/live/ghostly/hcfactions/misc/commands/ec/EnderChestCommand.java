package live.ghostly.hcfactions.misc.commands.ec;

import me.joeleoli.nucleus.command.Command;
import org.bukkit.entity.Player;

public class EnderChestCommand {
    @Command(names = {"enderchest", "ec"}, permissionNode = "hcf.enderchest")
    public static void enderchest(Player player) {
        player.openInventory(player.getEnderChest());
    }
}
