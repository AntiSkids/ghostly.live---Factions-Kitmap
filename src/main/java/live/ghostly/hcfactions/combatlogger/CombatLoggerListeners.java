package live.ghostly.hcfactions.combatlogger;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.deathsign.DeathSign;
import live.ghostly.hcfactions.event.sumo.SumoEvent;
import live.ghostly.hcfactions.factions.Faction;
import live.ghostly.hcfactions.factions.claims.Claim;
import live.ghostly.hcfactions.factions.type.PlayerFaction;
import live.ghostly.hcfactions.factions.type.SystemFaction;
import live.ghostly.hcfactions.mode.Mode;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.profile.cooldown.ProfileCooldownType;
import live.ghostly.hcfactions.profile.deathban.ProfileDeathban;
import live.ghostly.hcfactions.profile.deathmessage.ProfileDeathMessage;
import live.ghostly.hcfactions.profile.deathmessage.ProfileDeathMessageTemplate;
import live.ghostly.hcfactions.profile.fight.ProfileFight;
import live.ghostly.hcfactions.profile.fight.ProfileFightEffect;
import live.ghostly.hcfactions.profile.fight.ProfileFightEnvironment;
import live.ghostly.hcfactions.profile.fight.killer.type.ProfileFightEnvironmentKiller;
import live.ghostly.hcfactions.profile.fight.killer.type.ProfileFightPlayerKiller;
import live.ghostly.hcfactions.profile.fight.killstreaks.KillStreakType;
import live.ghostly.hcfactions.util.ItemNames;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.util.*;

public class CombatLoggerListeners implements Listener {

    private FactionsPlugin main;
    public static final String COMBAT_LOGGER_METADATA = "CombatLogger";
    @Getter private static Set<Entity> combatLoggers = new HashSet<>();

    public CombatLoggerListeners(FactionsPlugin main) {
        this.main = main;

        /*new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<CombatLogger, Long>> iterator = CombatLogger.getLoggersMap().entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<CombatLogger, Long> entry = iterator.next();
                    CombatLogger logger = entry.getKey();
                    long time = entry.getValue();

                    if (System.currentTimeMillis() - time > (main.getMainConfig().getInt("COMBAT_LOGGER.DESPAWN_TIME") * 1000)) {
                        logger.getEntity().remove();
                        iterator.remove();
                    }
                }
            }
        }.runTaskTimerAsynchronously(main, 20L, 20L);*/
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if(event.getPlayer().isDead()){
            return;
        }
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if(player.hasMetadata("staffmode")){
            return;
        }

        SumoEvent sumoEvent = FactionsPlugin.getInstance().getSumoEvent();

        if (sumoEvent.getPlayers().containsKey(player.getUniqueId())) {
            return;
        }

        Profile profile = Profile.getByPlayer(player);

        if (profile.getProtection() != null) {
            return;
        }

        for (Mode mode : Mode.getModes()) {
            if (mode.isSOTWActive()) {
                return;
            }
        }

        if (profile.isSafeLogout()) {
            return;
        }

        Claim claim = Claim.getProminentClaimAt(player.getLocation());

        if (claim != null) {
            Faction faction = claim.getFaction();

            if (faction instanceof SystemFaction) {
                SystemFaction systemFaction = (SystemFaction) faction;

                if (!systemFaction.isDeathban()) {
                    return;
                }
            }
        }

        ItemStack[] armor = event.getPlayer().getInventory().getArmorContents();
        ItemStack[] inv = event.getPlayer().getInventory().getContents();

        final Villager villager = (Villager) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.VILLAGER);

        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
        //villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100));

        if (event.getPlayer().hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
                // have to use .equals() as PotionEffectType isn't an enum
                if (potionEffect.getType().equals(PotionEffectType.FIRE_RESISTANCE)) {
                    villager.addPotionEffect(potionEffect);
                    break;
                }
            }
        }

        CombatLoggerMetadata metadata = new CombatLoggerMetadata();

        metadata.playerName = event.getPlayer().getName();
        metadata.playerUUID = event.getPlayer().getUniqueId();
        metadata.contents = inv;
        metadata.armor = armor;
        metadata.deathBanTime = ProfileDeathban.getDuration(player);
        metadata.hunger = player.getFoodLevel();
        metadata.effects = new ArrayList<>();

        for (PotionEffect effect : player.getActivePotionEffects()) {
            metadata.effects.add(new ProfileFightEffect(effect));
        }

        villager.setMetadata(COMBAT_LOGGER_METADATA, new FixedMetadataValue(FactionsPlugin.getInstance(), metadata));

        villager.setMaxHealth(calculateCombatLoggerHealth(event.getPlayer()));
        villager.setHealth(villager.getMaxHealth());

        villager.setCustomName(ChatColor.YELLOW.toString() + event.getPlayer().getName());
        villager.setCustomNameVisible(true);

        villager.setFallDistance(event.getPlayer().getFallDistance());
        villager.setRemoveWhenFarAway(false);
        villager.setVelocity(event.getPlayer().getVelocity());

        combatLoggers.add(villager);
        new BukkitRunnable() {

            public void run() {
                if (!villager.isDead() && villager.isValid()) {
                    combatLoggers.remove(villager);
                    villager.remove();
                }
            }

        }.runTaskLater(FactionsPlugin.getInstance(), 30 * 20L);
        //new CombatLogger(player);
    }

    @EventHandler
    public void onEntityDamageEventPlayer(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = Profile.getByPlayer(player);

            if (profile.getCooldownByType(ProfileCooldownType.LOGOUT) != null) {
                player.sendMessage(main.getLanguageConfig().getString("COMBAT_LOGGER.LOGOUT_CANCELLED"));
                profile.getCooldowns().remove(profile.getCooldownByType(ProfileCooldownType.LOGOUT));
                profile.setLogoutLocation(null);
            }
        }
    }


    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata(COMBAT_LOGGER_METADATA)) {
                if (entity.getCustomName() != null) {
                    entity.remove();
                }
            }
        }
    }

    @EventHandler
    public void onChunkUnloadEvent(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata(COMBAT_LOGGER_METADATA)) {
                entity.remove();
                combatLoggers.remove(entity);
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        Iterator<Entity> combatLoggerIterator = combatLoggers.iterator();

        while (combatLoggerIterator.hasNext()) {
            Villager villager = (Villager) combatLoggerIterator.next();

            if (villager.isCustomNameVisible() && ChatColor.stripColor(villager.getCustomName()).equals(event.getPlayer().getName())) {
                villager.remove();
                combatLoggerIterator.remove();
            }
        }

        if (profile.isCombatLogged()) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setExp(0);
            player.setHealth(0);
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeathEvent(EntityDeathEvent event) {
        if (!event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            return;
        }
        LivingEntity entity = event.getEntity();
        //CombatLogger logger = CombatLogger.getByEntity(entity);
        combatLoggers.remove(event.getEntity());
        CombatLoggerMetadata combatLoggerMetadata = (CombatLoggerMetadata) entity.getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

        if (combatLoggerMetadata != null) {
            event.getEntity().remove();
            Profile profile = new Profile(combatLoggerMetadata.getPlayerUUID());

            for (ItemStack item : combatLoggerMetadata.contents) {
                event.getDrops().add(item);
            }
            for (ItemStack item : combatLoggerMetadata.armor) {
                event.getDrops().add(item);
            }

            profile.getCooldowns().clear();

            PlayerFaction playerFaction = PlayerFaction.getByPlayerName(combatLoggerMetadata.getPlayerName());

            if (playerFaction != null) {
                playerFaction.freeze(FactionsPlugin.getInstance().getMainConfig().getInt("FACTION_GENERAL.FREEZE_DURATION"));
                playerFaction.setDeathsTillRaidable(playerFaction.getDeathsTillRaidable().subtract(BigDecimal.ONE));
                playerFaction.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("ANNOUNCEMENTS.FACTION.PLAYER_DEATH").replace("%PLAYER%", combatLoggerMetadata.getPlayerName()).replace("%DTR%", playerFaction.getDeathsTillRaidable() + "").replace("%MAX_DTR%", playerFaction.getMaxDeathsTillRaidable() + ""));
            }

            EntityDamageEvent damageEvent = entity.getLastDamageCause();

            entity.getWorld().strikeLightningEffect(entity.getLocation());

            if (!main.isKitmapMode()) {
                profile.setDeathban(new ProfileDeathban(combatLoggerMetadata.getDeathBanTime()));
            }

            profile.setCombatLogged(true);

            if (entity.getKiller() != null) {
                ProfileFight fight = new ProfileFight(UUID.randomUUID(), -1, System.currentTimeMillis(), combatLoggerMetadata.getContents(), combatLoggerMetadata.getArmor(), combatLoggerMetadata.getHunger(), combatLoggerMetadata.getEffects(), new ProfileFightPlayerKiller(entity.getKiller()), entity.getLocation());
                profile.getFights().add(fight);

                Profile.getByPlayer(entity.getKiller()).getFights().add(fight);

                Player killer = entity.getKiller();
                if (!FactionsPlugin.getInstance().isKitmapMode()) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), new DeathSign(killer.getName(), combatLoggerMetadata.getPlayerName()).toItemStack());
                } else {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate key kill 1 " + killer.getName());
                   killer.sendMessage(ChatColor.GREEN + "You have received (1) Crate Key.");
                }
                /*if (Bukkit.getPlayer(logger.getName()).hasPermission("vip.silver")) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemBuilder(Material.SKULL_ITEM).durability(3).owner(logger.getName()).build());
                }*/

                Profile killerProfile = Profile.getByPlayer(killer);

                killerProfile.setKillStreak((killerProfile.getKillStreak() + 1));
                killer.incrementStatistic(Statistic.PLAYER_KILLS, 1);

                for (KillStreakType killStreakType : KillStreakType.values()) {
                    if (killerProfile.getKillStreak() == killStreakType.getCount()) {
                        Bukkit.broadcastMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KILL_STREAK.MESSAGE").replace("%PLAYER%", killer.getName()).replace("%COUNT%", killerProfile.getKillStreak() + ""));

                        for (ItemStack item : killStreakType.getItems()) {
                            killer.getInventory().addItem(item);
                        }
                    }
                }

                String weapon = "their fists";

                if (profile.getLastDamager() != null && profile.getLastDamager().getKey() != null && profile.getLastDamager().getKey().equals(killer.getUniqueId())) {
                    ItemStack item = profile.getLastDamager().getValue();

                    if (item != null) {
                        if (item.getItemMeta().hasDisplayName()) {
                            weapon = item.getItemMeta().getDisplayName();
                        } else {
                            weapon = ItemNames.lookup(item);
                        }
                    }
                } else {
                    if (killer.getItemInHand() != null && killer.getItemInHand().getType() != Material.AIR) {
                        if (killer.getItemInHand().getItemMeta().hasDisplayName()) {
                            weapon = killer.getItemInHand().getItemMeta().getDisplayName();
                        } else {
                            weapon = ItemNames.lookup(killer.getItemInHand());
                        }
                    }
                }

                new ProfileDeathMessage(ProfileDeathMessageTemplate.LOGGER, profile, killerProfile, weapon);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        profile.save();
                        Profile.getProfilesMap().remove(profile.getUuid());
                    }
                }.runTaskAsynchronously(this.main);

                return;
            }

            if (damageEvent == null) {
                new ProfileDeathMessage(ProfileDeathMessageTemplate.CUSTOM, profile);
                profile.getFights().add(new ProfileFight(UUID.randomUUID(), -1, System.currentTimeMillis(), combatLoggerMetadata.getContents(), combatLoggerMetadata.getArmor(), combatLoggerMetadata.getHunger(), combatLoggerMetadata.getEffects(), new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM), entity.getLocation()));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        profile.save();
                        Profile.getProfilesMap().remove(profile.getUuid());
                    }
                }.runTaskAsynchronously(this.main);

                return;
            }

            EntityDamageEvent.DamageCause cause = damageEvent.getCause();

            if (cause == EntityDamageEvent.DamageCause.PROJECTILE || cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.POISON || cause == EntityDamageEvent.DamageCause.MAGIC || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                return;
            }

            try {
                profile.getFights().add(new ProfileFight(UUID.randomUUID(), -1, System.currentTimeMillis(), combatLoggerMetadata.getContents(), combatLoggerMetadata.getArmor(), combatLoggerMetadata.getHunger(), combatLoggerMetadata.getEffects(), new ProfileFightEnvironmentKiller(ProfileFightEnvironment.valueOf(cause.name().toUpperCase())), entity.getLocation()));
            } catch (Exception ignored) {
                profile.getFights().add(new ProfileFight(UUID.randomUUID(), -1, System.currentTimeMillis(), combatLoggerMetadata.getContents(), combatLoggerMetadata.getArmor(), combatLoggerMetadata.getHunger(), combatLoggerMetadata.getEffects(), new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM), entity.getLocation()));
            }

            ProfileDeathMessageTemplate template;

            try {
                template = ProfileDeathMessageTemplate.valueOf(cause.name());
            } catch (Exception exception) {
                return;
            }

            new ProfileDeathMessage(template, profile);

            profile.save();
            Profile.getProfilesMap().remove(profile.getUuid());
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if(!event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)){
            return;
        }
        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if(damager == null){
            return;
        }

        PlayerFaction damagerFaction = PlayerFaction.getByPlayer(damager);

        if(damagerFaction == null){
            return;
        }

        CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

        if (metadata != null) {

            if(damagerFaction.getAllPlayerUuids().contains(metadata.getPlayerUUID())){
                damager.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("FACTION_OTHER.CANNOT_DAMAGE_FRIENDLY").replace("%PLAYER%", metadata.getPlayerName()));
                event.setCancelled(true);
                return;
            }

            if(FactionsPlugin.getInstance().getMainConfig().getBoolean("ALLIES.ENABLED")){
                Player finalDamager = damager;
                damagerFaction.getAllies().forEach(ally -> {
                    if(ally.getAllyUuids().contains(metadata.getPlayerUUID()) && !FactionsPlugin.getInstance().getMainConfig().getBoolean("ALLIES.DAMAGE_ALLIES")){
                        finalDamager.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("FACTION_OTHER.CANNOT_DAMAGE_ALLY").replace("%PLAYER%", metadata.getPlayerName()));
                        event.setCancelled(true);
                    }
                });
            }
        }
    }

    public double calculateCombatLoggerHealth(Player player) {
        int potions = 0;
        boolean gapple = false;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType() == Material.POTION && itemStack.getDurability() == (short) 16421) {
                potions++;
            } else if (!gapple && itemStack.getType() == Material.GOLDEN_APPLE && itemStack.getDurability() == (short) 1) {
                // Only let the player have one gapple count.
                potions += 15;
                gapple = true;
            }
        }

        return ((potions * 3.5D) + player.getHealth());
    }

    @Getter
    public static class CombatLoggerMetadata {

        private ItemStack[] contents;
        private ItemStack[] armor;
        private String playerName;
        private UUID playerUUID;
        private long deathBanTime;
        private double hunger;
        private List<ProfileFightEffect> effects;

    }

}
