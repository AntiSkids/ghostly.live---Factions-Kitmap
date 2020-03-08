package live.ghostly.hcfactions.profile.fight;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.factions.type.PlayerFaction;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.profile.cooldown.ProfileCooldownType;
import live.ghostly.hcfactions.profile.fight.killer.ProfileFightKiller;
import live.ghostly.hcfactions.profile.fight.killer.type.ProfileFightEnvironmentKiller;
import live.ghostly.hcfactions.profile.fight.killer.type.ProfileFightPlayerKiller;
import live.ghostly.hcfactions.profile.fight.killstreaks.KillStreakType;
import live.ghostly.hcfactions.profile.kit.ProfileKit;
import live.ghostly.hcfactions.profile.kit.ProfileKitCooldown;
import live.ghostly.hcfactions.profile.kit.ability.ProfileKitAbility;
import live.ghostly.hcfactions.util.Style;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.math.BigDecimal;

public class ProfileFightListeners implements Listener {

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            /*new BukkitRunnable() {
                @Override
                public void run() {
                    player.setNoDamageTicks(19);
                }
            }.runTaskLater(FactionsPlugin.getInstance(), 1L);*/

            if (player.getHealth() - event.getFinalDamage() <= 0) {
                Profile profile = Profile.getByPlayer(player);

                LivingEntity damager;

                if (event.getDamager() instanceof LivingEntity) {
                    damager = (LivingEntity) event.getDamager();
                } else if (event.getDamager() instanceof Projectile) {
                    if (((Projectile) event.getDamager()).getShooter() != null) {
                        damager = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
                    } else {
                        damager = null;
                    }
                } else {
                    damager = null;
                }

                if (damager == null) {
                    return;
                }

                if (profile.isCombatLogged()) {
                    return;
                }

                if (!(damager instanceof Player)) {
                    profile.getFights().add(new ProfileFight(player, new ProfileFightKiller(damager.getType(), damager.getType().getName())));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            profile.save();
                        }
                    }.runTaskAsynchronously(FactionsPlugin.getInstance());
                }

            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = Profile.getByPlayer(player);
        EntityDamageEvent damageEvent = player.getLastDamageCause();

        if (profile.getKillStreak() > 0) {
            profile.setKillStreak(0);
        }

        int balance = profile.getBalance();
        if (!FactionsPlugin.getInstance().isKitmapMode()) {
            if (balance > 0) {
                profile.setBalance(0);
            }
        }

        if (profile.isCombatLogged()) {

            event.setDeathMessage(null);

            PlayerFaction playerFaction = PlayerFaction.getByPlayerName(player.getName());
            if (playerFaction != null) {
                playerFaction.setDeathsTillRaidable(playerFaction.getDeathsTillRaidable().add(BigDecimal.ONE));
            }

            return;
        }

        if (profile.getCooldownByType(ProfileCooldownType.SPAWN_TAG) != null) {
            profile.getCooldowns().remove(profile.getCooldownByType(ProfileCooldownType.SPAWN_TAG));
        }

        player.getWorld().strikeLightningEffect(player.getLocation());

        if (player.getKiller() != null) {
            ProfileFight fight = new ProfileFight(player, new ProfileFightPlayerKiller(player.getKiller()));
            profile.getFights().add(fight);

            Profile killerProfile = Profile.getByPlayer(player.getKiller());

            PlayerFaction killerFaction = killerProfile.getFaction();

            if (killerFaction != null && killerFaction.getFocusPlayer() != null && killerFaction.getFocusPlayer() == player.getUniqueId()) {
                killerFaction.setFocusPlayer(null);
                killerFaction.sendMessage(ChatColor.YELLOW + "Focus has been removed from " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + ".");

                for (Player member : killerFaction.getOnlinePlayers()) {

                    Profile memberProfile = Profile.getByPlayer(member);

                    if (memberProfile != null) {
                        memberProfile.sendEnemyTab();
                    }
                }
            }

            killerProfile.getFights().add(fight);

            if (FactionsPlugin.getInstance().isKitmapMode()) {

                killerProfile.setKillStreak((killerProfile.getKillStreak() + 1));

                for (KillStreakType killStreakType : KillStreakType.values()) {
                    if (killerProfile.getKillStreak() == killStreakType.getCount()) {
                        Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KILL_STREAK.MESSAGE").replace("%PLAYER%", player.getKiller().getName()).replace("%COUNT%", killerProfile.getKillStreak() + ""));

                        for (ItemStack item : killStreakType.getItems()) {
                            player.getKiller().getInventory().addItem(item);
                        }
                    }
                }

                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate key kill 1 " + player.getKiller().getName());
                player.getKiller().sendMessage(ChatColor.GREEN + "You have received (1) Crate Key.");

            }

            if (FactionsPlugin.getInstance().isKitmapMode()) {
                int toGive = FactionsPlugin.getInstance().getMainConfig().getInt("KITMAP_KILL.MONEY");
                Player killer = player.getKiller();
                if (killer.hasPermission("killreward.300")) {
                    toGive = 300;
                } else if (killer.hasPermission("killreward.250")) {
                    toGive = 200;
                } else if (killer.hasPermission("killreward.200")) {
                    toGive = 200;
                } else if (killer.hasPermission("killreward.150")) {
                    toGive = 150;
                }
                killerProfile.setBalance((killerProfile.getBalance() + toGive));
                player.getKiller().sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KITMAP_KILL.MESSAGE").replace("%MONEY%", toGive + "").replace("%PLAYER%", player.getName()));
            } else {

                if (balance > 0) {
                    killerProfile.setBalance((killerProfile.getBalance() + balance));
                    player.getKiller().sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KITMAP_KILL.MESSAGE").replace("%MONEY%", balance + "").replace("%PLAYER%", player.getName()));
                }
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    profile.save();
                }
            }.runTaskAsynchronously(FactionsPlugin.getInstance());
            return;
        }

        event.getDrops().clear();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getType() != Material.AIR) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }

        if (damageEvent == null) {
            profile.getFights().add(new ProfileFight(player, new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM)));
            new BukkitRunnable() {
                @Override
                public void run() {
                    profile.save();
                }
            }.runTaskAsynchronously(FactionsPlugin.getInstance());
            return;
        }

        DamageCause cause = damageEvent.getCause();

        if (cause == DamageCause.PROJECTILE || cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.POISON || cause == DamageCause.MAGIC || cause == DamageCause.ENTITY_EXPLOSION) {
            return;
        }

        try {
            profile.getFights().add(new ProfileFight(player, new ProfileFightEnvironmentKiller(ProfileFightEnvironment.valueOf(cause.name().toUpperCase()))));
            new BukkitRunnable() {
                @Override
                public void run() {
                    profile.save();
                }
            }.runTaskAsynchronously(FactionsPlugin.getInstance());
        } catch (Exception ignored) {
            profile.getFights().add(new ProfileFight(player, new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM)));
            new BukkitRunnable() {
                @Override
                public void run() {
                    profile.save();
                }
            }.runTaskAsynchronously(FactionsPlugin.getInstance());
        }

        player.spigot().respawn();
    }

    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof FishHook) {
            event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(1.025));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void fishEvent(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        ProfileKit kit = profile.getKit();
        ProfileKitCooldown cooldown = profile.getKitCooldownByType(ProfileKitAbility.FISHING_ROD);
        Entity caught = event.getCaught();
        if (kit == ProfileKit.ROGUE && caught instanceof Player) {
            if (cooldown != null) {
                player.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KIT.COOLDOWN").replace("%TIME%", cooldown.getTimeLeft()));
            } else {
                pullEntityToLocation(caught, player.getLocation());
                player.sendMessage(Style.translate("&e&lGET OVER HERE!"));
                profile.getKitCooldowns().add(new ProfileKitCooldown(ProfileKitAbility.FISHING_ROD, 15));
            }
        }
    }

    private void pullEntityToLocation(Entity entity, Location location) {
        Location entityLoc = entity.getLocation();
        entityLoc.setY(entityLoc.getY() + 0.5D);
        entity.teleport(entityLoc);
        double g = -0.08D;
        double t = location.distance(entityLoc);
        double v_x = (1.0D + 0.07D * t) * (location.getX() - entityLoc.getX()) / t;
        double v_y = (1.0D + 0.03D * t) * (location.getY() - entityLoc.getY()) / t - 0.5D * g * t;
        double v_z = (1.0D + 0.07D * t) * (location.getZ() - entityLoc.getZ()) / t;
        Vector v = entity.getVelocity();
        v.setX(v_x);
        v.setY(v_y);
        v.setZ(v_z);
        entity.setVelocity(v);
        //addNoFall(e, 100);
    }

}
