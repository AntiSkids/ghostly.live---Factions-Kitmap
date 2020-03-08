package live.ghostly.hcfactions.profile.kit;

import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.factions.claims.Claim;
import live.ghostly.hcfactions.factions.type.SystemFaction;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.profile.cooldown.ProfileCooldown;
import live.ghostly.hcfactions.profile.cooldown.ProfileCooldownType;
import live.ghostly.hcfactions.profile.kit.events.ArcherTagEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

public class ProfileKitActionListeners implements Listener {


    private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");
    private static final int ARCHER_PERCENTAGE_DAMAGE = FactionsPlugin.getInstance().getMainConfig().getInt("KIT.ARCHER_TAG.PERCENTAGE_DAMAGE");
    private static final long ROGUE_BACKSTAB_COOLDOWN = FactionsPlugin.getInstance().getMainConfig().getInt("KIT.ROGUE_BACKSTAB.COOLDOWN");
    private static final int ROGUE_HIT_DAMAGE = FactionsPlugin.getInstance().getMainConfig().getInt("KIT.ROGUE_BACKSTAB.HIT_DAMAGE");
    private static final int ROGUE_SLOWNESS_DURATION = FactionsPlugin.getInstance().getMainConfig().getInt("KIT.ROGUE_BACKSTAB.SLOWNESS_DURATION");
    private static HashMap<UUID, Long> tagged = new HashMap<UUID, Long>();
    private HashMap<UUID, Location> locations = new HashMap<>();
    private HashMap<UUID, Long> backstabCooldown = new HashMap<UUID, Long>();

    public static HashMap<UUID, Long> getTagged() {
        return tagged;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        Profile damaged2 = Profile.getByPlayer(damaged);
        ProfileKit kit = Profile.getByPlayer(damager).getKit();

        if (kit != null && kit == ProfileKit.ROGUE && kit.isWearingArmor(damager)) {

            ItemStack stack = damager.getItemInHand();
            double damage = damaged.getLocation().getDirection().dot(damager.getLocation().getDirection()) + 1.0D;
            if ((damage > 0.85D) && (stack != null) && (stack.getType() == Material.GOLD_SWORD) && (stack.getEnchantments().isEmpty())) {

                if (this.backstabCooldown.containsKey(damager.getUniqueId()) && this.backstabCooldown.get(damager.getUniqueId()) > System.currentTimeMillis()) {
                    String timeLeft = this.getTimeLeft(damager);
                    damager.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KIT.ROGUE_BACKSTAB_COOLDOWN").replace("%COOLDOWN%", timeLeft));
                    return;
                }
                if (damaged2.getProtection() != null) {


                } else {
                    event.setDamage(ROGUE_HIT_DAMAGE);
                    damaged.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KIT.ROGUE_BACKSTAB_ATTACKED").replace("%PLAYER%", damager.getName()));
                    damaged.playSound(damaged.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                    damager.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KIT.ROGUE_BACKSTAB_ATTACKER").replace("%PLAYER%", damaged.getName()));
                    damager.setItemInHand(new ItemStack(Material.AIR, 1));
                    damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                    damager.updateInventory();
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, ROGUE_SLOWNESS_DURATION * 20, FactionsPlugin.getInstance().getMainConfig().getInt("KIT.ROGUE_BACKSTAB.SLOWNESS_LEVEL") - 1), true);
                }
                event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0D);
                this.backstabCooldown.put(damager.getUniqueId(), System.currentTimeMillis() + ROGUE_BACKSTAB_COOLDOWN);

            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        Profile profile = Profile.getByPlayer(player);

        if (profile == null) {
            return;
        }

        ProfileKit kit = profile.getKit();

        if (kit != null && kit == ProfileKit.ARCHER && kit.isWearingArmor(player) && player.getItemInHand().getType() == Material.BOW) {

            Claim claim = Claim.getProminentClaimAt(player.getLocation());

            if (claim != null && claim.getFaction() instanceof SystemFaction && !((SystemFaction) claim.getFaction()).isDeathban()) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {

        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {

            Arrow arrow = (Arrow) event.getEntity();
            Player player = (Player) event.getEntity().getShooter();

            ProfileKit kit = Profile.getByPlayer(player).getKit();

            if (kit != null && kit == ProfileKit.ARCHER && kit.isWearingArmor(player)) {
                this.locations.put(player.getUniqueId(), player.getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();
        if (entity instanceof Player && damager instanceof Arrow) {
            final Arrow arrow = (Arrow) damager;
            final ProjectileSource source = arrow.getShooter();
            if (source instanceof Player) {
                final Player damaged = (Player) event.getEntity();
                final Player shooter = (Player) source;

                Profile profile = Profile.getByPlayer(shooter);
                Profile damagedProfile = Profile.getByPlayer(damaged);

                if (profile == null || damagedProfile == null) {
                    return;
                }

                ProfileKit kit = profile.getKit();

                if (kit != null && kit == ProfileKit.ARCHER && kit.isWearingArmor(shooter)) {

                    if (damaged.getName().equalsIgnoreCase(shooter.getName())) {
                        return;
                    }

                    if (profile.getFaction() != null && damagedProfile.getFaction() != null && (profile.getFaction() == damagedProfile.getFaction() || profile.getFaction().getAllies().contains(damagedProfile.getFaction()))) {
                        return;
                    }

                    if (damagedProfile.getKit() != null && damagedProfile.getKit() == ProfileKit.ARCHER) {
                        return;
                    }

                    ProfileCooldown cooldown = damagedProfile.getCooldownByType(ProfileCooldownType.ARCHER_TAG);

                    if (cooldown != null) {
                        damagedProfile.getCooldowns().remove(cooldown);
                    }

                    damagedProfile.getCooldowns().add(new ProfileCooldown(ProfileCooldownType.ARCHER_TAG, ProfileCooldownType.ARCHER_TAG.getDuration()));
                    Bukkit.getServer().getPluginManager().callEvent(new ArcherTagEvent(shooter, damaged));

					/*Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(FactionsPlugin.getInstance(), () -> {
						Profile.sendGlobalTabUpdate();
						profile.updateTab();
					}, 20L);*/

                    Claim claim = Claim.getProminentClaimAt(shooter.getLocation());


                    if (this.locations.get(shooter.getUniqueId()) == null) {
                        return;
                    }

                    int distance = (int) this.locations.get(shooter.getUniqueId()).distance(damaged.getLocation());
                    this.locations.remove(shooter.getUniqueId());

                    if (claim != null && claim.getFaction() instanceof SystemFaction && !((SystemFaction) claim.getFaction()).isDeathban()) {
                        return;
                    }

                    damaged.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KIT.ARCHER_TAG_ATTACKED").replace("%PLAYER%", shooter.getName()));
                    shooter.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KIT.ARCHER_TAG_ATTACKER").replace("%PLAYER%", damaged.getName()).replace("%BLOCKS%", distance > 1 ? distance + " blocks" : distance + " block"));
                    this.locations.remove(shooter.getUniqueId());

                    damaged.setHealth(damaged.getHealth() - (distance / 30));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHit(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            final Player entity = (Player) event.getEntity();


            Profile profile = Profile.getByPlayer(entity);

            if (profile == null) {
                return;
            }

            ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.ARCHER_TAG);
            if (cooldown != null) {
                event.setDamage(event.getDamage() * 1.25);
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {

        Player player = event.getEntity();

        Profile profile = Profile.getByPlayer(player);

        if (profile == null) {
            return;
        }

        ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.ARCHER_TAG);

        if (cooldown != null) {
            profile.getCooldowns().remove(cooldown);
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (this.backstabCooldown.containsKey(player.getUniqueId())) {
            this.backstabCooldown.remove(player.getUniqueId());
        }

        if (this.locations.containsKey(player.getUniqueId())) {
            this.locations.remove(player.getUniqueId());
        }
    }

    private String getTimeLeft(Player damager) {
        return SECONDS_FORMATTER.format(((((backstabCooldown.get(damager.getUniqueId()) - System.currentTimeMillis()) / 1000.0f))));
    }
}
