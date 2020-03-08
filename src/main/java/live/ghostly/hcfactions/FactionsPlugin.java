package live.ghostly.hcfactions;

import live.ghostly.hcfactions.blockoperation.BlockOperationModifier;
import live.ghostly.hcfactions.blockoperation.BlockOperationModifierListeners;
import live.ghostly.hcfactions.board.FactionsBoardAdapter;
import live.ghostly.hcfactions.claimwall.ClaimWallListeners;
import live.ghostly.hcfactions.combatlogger.CombatLogger;
import live.ghostly.hcfactions.combatlogger.CombatLoggerListeners;
import live.ghostly.hcfactions.combatlogger.commands.CombatLoggerCommand;
import live.ghostly.hcfactions.crate.Crate;
import live.ghostly.hcfactions.crate.CrateListeners;
import live.ghostly.hcfactions.crate.command.CrateCommand;
import live.ghostly.hcfactions.crowbar.CrowbarListeners;
import live.ghostly.hcfactions.deathlookup.DeathLookupCommand;
import live.ghostly.hcfactions.deathlookup.DeathLookupListeners;
import live.ghostly.hcfactions.deathsign.DeathSignListeners;
import live.ghostly.hcfactions.economysign.EconomySignListeners;
import live.ghostly.hcfactions.enchantmentlimiter.EnchantmentLimiterListeners;
import live.ghostly.hcfactions.event.Event;
import live.ghostly.hcfactions.event.EventManager;
import live.ghostly.hcfactions.event.citadel.CitadelEvent;
import live.ghostly.hcfactions.event.citadel.CitadelEventListeners;
import live.ghostly.hcfactions.event.citadel.command.*;
import live.ghostly.hcfactions.event.citadel.procedure.CitadelCreateProcedureListeners;
import live.ghostly.hcfactions.event.citadel.procedure.command.CitadelCreateProcedureCommand;
import live.ghostly.hcfactions.event.citadel.procedure.command.CitadelRemoveCommand;
import live.ghostly.hcfactions.event.glowstone.GlowstoneEvent;
import live.ghostly.hcfactions.event.glowstone.GlowstoneEventListeners;
import live.ghostly.hcfactions.event.glowstone.command.GlowstoneForceCommand;
import live.ghostly.hcfactions.event.glowstone.procedure.GlowstoneCreateProcedureListeners;
import live.ghostly.hcfactions.event.glowstone.procedure.command.GlowstoneProcedureCommand;
import live.ghostly.hcfactions.event.glowstone.procedure.command.GlowstoneRemoveCommand;
import live.ghostly.hcfactions.event.koth.KothEvent;
import live.ghostly.hcfactions.event.koth.KothEventListeners;
import live.ghostly.hcfactions.event.koth.command.*;
import live.ghostly.hcfactions.event.koth.procedure.KothCreateProcedureListeners;
import live.ghostly.hcfactions.event.koth.procedure.command.KothCreateProcedureCommand;
import live.ghostly.hcfactions.event.koth.procedure.command.KothRemoveCommand;
import live.ghostly.hcfactions.event.sumo.SumoEvent;
import live.ghostly.hcfactions.event.sumo.command.EndSumoCommand;
import live.ghostly.hcfactions.event.sumo.command.HostSumoCommand;
import live.ghostly.hcfactions.event.sumo.command.JoinSumoCommand;
import live.ghostly.hcfactions.event.sumo.command.SumoSetSpawn;
import live.ghostly.hcfactions.event.sumo.listeners.SumoListeners;
import live.ghostly.hcfactions.factions.Faction;
import live.ghostly.hcfactions.factions.claims.ClaimListeners;
import live.ghostly.hcfactions.factions.claims.ClaimPillar;
import live.ghostly.hcfactions.factions.claims.CustomMovementHandler;
import live.ghostly.hcfactions.factions.commands.*;
import live.ghostly.hcfactions.factions.commands.admin.*;
import live.ghostly.hcfactions.factions.commands.coleader.FactionDemoteCommand;
import live.ghostly.hcfactions.factions.commands.coleader.FactionPromoteCommand;
import live.ghostly.hcfactions.factions.commands.coleader.FactionRenameCommand;
import live.ghostly.hcfactions.factions.commands.coleader.FactionUnclaimCommand;
import live.ghostly.hcfactions.factions.commands.leader.FactionDisbandCommand;
import live.ghostly.hcfactions.factions.commands.leader.FactionLeaderCommand;
import live.ghostly.hcfactions.factions.commands.officer.*;
import live.ghostly.hcfactions.factions.commands.system.FactionColorCommand;
import live.ghostly.hcfactions.factions.commands.system.FactionCreateSystemCommand;
import live.ghostly.hcfactions.factions.commands.system.FactionToggleDeathbanCommand;
import live.ghostly.hcfactions.factions.commands.system.FactionTogglePvPWallCommand;
import live.ghostly.hcfactions.factions.type.PlayerFaction;
import live.ghostly.hcfactions.files.ConfigFile;
import live.ghostly.hcfactions.inventory.command.CloneInventoryCommand;
import live.ghostly.hcfactions.inventory.command.GiveInventoryCommand;
import live.ghostly.hcfactions.inventory.command.LastInventoryCommand;
import live.ghostly.hcfactions.itemdye.ItemDye;
import live.ghostly.hcfactions.itemdye.ItemDyeListeners;
import live.ghostly.hcfactions.kits.Kit;
import live.ghostly.hcfactions.kits.KitListeners;
import live.ghostly.hcfactions.misc.commands.*;
import live.ghostly.hcfactions.misc.commands.economy.AddBalanceCommand;
import live.ghostly.hcfactions.misc.commands.economy.BalanceCommand;
import live.ghostly.hcfactions.misc.commands.economy.PayCommand;
import live.ghostly.hcfactions.misc.commands.economy.SetBalanceCommand;
import live.ghostly.hcfactions.misc.listeners.*;
import live.ghostly.hcfactions.misc.listeners.fixes.BeaconStreanthFixListener;
import live.ghostly.hcfactions.misc.listeners.fixes.BlockHitFixListener;
import live.ghostly.hcfactions.misc.listeners.fixes.EnchantingTableFix;
import live.ghostly.hcfactions.misc.listeners.fixes.RecipeListeners;
import live.ghostly.hcfactions.mode.Mode;
import live.ghostly.hcfactions.mode.ModeListeners;
import live.ghostly.hcfactions.mode.command.ModeCommand;
import live.ghostly.hcfactions.potionlimiter.PotionLimiterListeners;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.profile.ProfileAutoSaver;
import live.ghostly.hcfactions.profile.ProfileListeners;
import live.ghostly.hcfactions.profile.cooldown.ProfileCooldownListeners;
import live.ghostly.hcfactions.profile.fight.command.KillStreakCommand;
import live.ghostly.hcfactions.profile.kit.ProfileKitActionListeners;
import live.ghostly.hcfactions.profile.options.command.ProfileOptionsCommand;
import live.ghostly.hcfactions.profile.ore.ProfileOreCommand;
import live.ghostly.hcfactions.profile.protection.ProfileProtection;
import live.ghostly.hcfactions.profile.protection.command.ProfileProtectionCommand;
import live.ghostly.hcfactions.statracker.StatTrackerListeners;
import live.ghostly.hcfactions.subclaim.SubclaimListeners;
import live.ghostly.hcfactions.util.command.CommandFramework;
import live.ghostly.hcfactions.util.database.FactionsDatabase;
import live.ghostly.hcfactions.util.player.PlayerUtility;
import live.ghostly.hcfactions.util.player.SimpleOfflinePlayer;
import lombok.Getter;
import lombok.Setter;
import me.joeleoli.frame.Frame;
import me.joeleoli.nucleus.command.CommandHandler;
import me.norxir.spigot.FrozenSpigot;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@Getter
public class FactionsPlugin extends JavaPlugin {

    @Getter
    private static FactionsPlugin instance;

    private CommandFramework framework;
    private FactionsDatabase factionsDatabase;
    private ConfigFile mainConfig, scoreboardConfig, languageConfig, kothScheduleConfig, citadelScheduleConfig;
    @Setter
    private boolean loaded;
    @Setter
    private boolean kitmapMode;
    private Chat chat;
    private SumoEvent sumoEvent;

    public void onEnable() {
        instance = this;

        this.mainConfig = new ConfigFile(this, "config");
        this.languageConfig = new ConfigFile(this, "lang");
        this.scoreboardConfig = new ConfigFile(this, "scoreboard");
        this.kothScheduleConfig = new ConfigFile(this, "koth-schedule");
        this.citadelScheduleConfig = new ConfigFile(this, "citadel-schedule");
        this.factionsDatabase = new FactionsDatabase(this);
        this.kitmapMode = this.mainConfig.getBoolean("KITMAP_MODE");

        for (Player player : PlayerUtility.getOnlinePlayers()) {
            new Profile(player.getUniqueId());
        }

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.hasMetadata("CombatLogger")) {
                    if (entity instanceof LivingEntity) {
                        entity.remove();
                    }
                }
            }
        }

        RegisteredServiceProvider<Chat> chatProvider = this.getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        this.framework = new CommandFramework(this);

        SimpleOfflinePlayer.load(this);
        Faction.load();
        Mode.load();
        KothEvent.load();
        CitadelEvent.load();
        GlowstoneEvent.load();
        Crate.load();
        Kit.load();
        BlockOperationModifier.run();
        ProfileProtection.run(this);

        registerRecipes();
        registerListeners();
        registerCommands();

        PlayerFaction.runTasks();

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new ProfileAutoSaver(this), 5000L, 5000L);

        FrozenSpigot.INSTANCE.addMovementHandler(new CustomMovementHandler());

        //CorePlugin.getInstance().useTabList();
        //CorePlugin.getInstance().getTabListManager().getTabList().setHead(UUID.fromString("6b22037d-c043-4271-94f2-adb00368bf16"));

        new Frame(this, (new FactionsBoardAdapter(this)));
        // this.getServer().getScheduler().runTaskTimerAsynchronously(this, new TabListRunnable(), 20L, 20L);

        sumoEvent = new SumoEvent();
    }

    public void onDisable() {
        Faction.save();

        for (Player player : PlayerUtility.getOnlinePlayers()) {
            Profile profile = Profile.getByPlayer(player);

            if (profile.getClaimProfile() != null) {
                profile.getClaimProfile().removePillars();
            }

            for (ClaimPillar claimPillar : profile.getMapPillars()) {
                claimPillar.remove();
            }

            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        try {
            SimpleOfflinePlayer.save(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Profile profile : Profile.getProfiles()) {
            profile.save();
        }

        for (Mode mode : Mode.getModes()) {
            mode.save();
        }

        for (Entity logger : CombatLoggerListeners.getCombatLoggers()) {
            logger.remove();
        }

        for (Event event : EventManager.getInstance().getEvents()) {
            if (event instanceof KothEvent) {
                ((KothEvent) event).save();
            } else if (event instanceof GlowstoneEvent) {
                ((GlowstoneEvent) event).save();
            } else if (event instanceof CitadelEvent) {
                ((CitadelEvent) event).save();
            }
        }

        for (Crate crate : Crate.getCrates()) {
            crate.save();
        }

        for (Kit kit : Kit.getKits()) {
            kit.save();
        }

        factionsDatabase.getClient().close();
    }

    private void registerRecipes() {
        for (Material material : Material.values()) {
            if (material.name().contains("CHESTPLATE") || material.name().contains("SWORD") || material.name().contains("LEGGINGS") || material.name().contains("BOOTS") || material.name().contains("HELMET") || material.name().contains("AXE") || material.name().contains("SPADE")) {
                for (ItemDye dye : ItemDye.values()) {
                    Bukkit.addRecipe(ItemDye.getRecipe(material, dye));
                }
            }
        }

        Bukkit.addRecipe(new ShapelessRecipe(new ItemStack(Material.EXP_BOTTLE)).addIngredient(1, Material.GLASS_BOTTLE));
    }

    private void registerCommands() {
        new ProfileProtectionCommand();
        new ProfileOreCommand();
        new CloneInventoryCommand();
        new LastInventoryCommand();
        new GiveInventoryCommand();
        new DeathLookupCommand();
        //new ProfileKitCommand();
        new KillStreakCommand();
        new CombatLoggerCommand();

        new ModeCommand();

        new SetSpawnEnd();
        new SetNetherExit();
        new SetEndExit();

        new KothCommand();
        new KothScheduleCommand();
        new KothCreateProcedureCommand();
        new KothRemoveCommand();
        new KothStartCommand();
        new KothStopCommand();

        new KothLootCommand();


        new CitadelCommand();
        new CitadelScheduleCommand();
        new CitadelCreateProcedureCommand();
        new CitadelRemoveCommand();
        new CitadelStartCommand();
        new CitadelStopCommand();

        new CitadelLootCommand();

        new GlowstoneProcedureCommand();
        new GlowstoneRemoveCommand();
        new GlowstoneForceCommand();

        new StatsCommand();
        new BalanceCommand();
        new ReclaimCommand();
        new ReclaimRemoveCommand();
        new StackCommand();
        new CobbleCommand();
        new PayCommand();
        new SetBalanceCommand();
        new AddBalanceCommand();
        new HelpCommand();
        new SpawnCommand();
        new ProfileOptionsCommand();
        new CrateCommand();
        new MapKitCommand();
        new RenameCommand();
        new PlayTimeCommand();
        new TellLocationCommand();
        new FocusCommand();
        new SetGKitCommand();
        new MedicReviveCommand();
        new CampCommand();

        new FactionHelpCommand();
        new FactionDisbandCommand();
        new FactionCreateCommand();
        new FactionDisbandAllCommand();
        new FactionInviteCommand();
        new FactionJoinCommand();
        new FactionRenameCommand();
        new FactionPromoteCommand();
        new FactionDemoteCommand();
        new FactionLeaderCommand();
        new FactionUninviteCommand();
        new FactionChatCommand();
        new FactionSetHomeCommand();
        new FactionMessageCommand();
        new FactionAnnouncementCommand();
        new FactionLeaveCommand();
        new FactionShowCommand();
        new FactionKickCommand();
        new FactionInvitesCommand();
        new FactionDepositCommand();
        new FactionWithdrawCommand();
        new FactionClaimCommand();
        new FactionMapCommand();
        new FactionUnclaimCommand();
        new FactionListCommand();
        new FactionHomeCommand();
        new FactionStuckCommand();
        new FactionCreateSystemCommand();
        new FactionToggleDeathbanCommand();
        new FactionTogglePvPWallCommand();
        new FactionColorCommand();
        new FactionFreezeCommand();
        new FactionThawCommand();
        new FactionSetDtrCommand();
        new FactionAdminCommand();

        if (isKitmapMode()) {
            new SumoSetSpawn();
            new HostSumoCommand();
            new JoinSumoCommand();
            new EndSumoCommand();
        }
        CommandHandler.loadCommandsFromPackage(this, "live.ghostly.hcfactions.misc.commands.ec");

        if (this.mainConfig.getBoolean("FACTION_GENERAL.ALLIES.ENABLED")) {
            new FactionAllyCommand();
        }

        if (mainConfig.getBoolean("FACTION_GENERAL.ALLIES.ENABLED")) {
            new FactionEnemyCommand();
        }
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ProfileListeners(this), this);
        //pluginManager.registerEvents(new MobStackListeners(), this);
        pluginManager.registerEvents(new CrowbarListeners(), this);
        pluginManager.registerEvents(new EconomySignListeners(), this);
        pluginManager.registerEvents(new DeathSignListeners(), this);
        pluginManager.registerEvents(new StatTrackerListeners(), this);
        pluginManager.registerEvents(new ProfileCooldownListeners(), this);
        pluginManager.registerEvents(new ProfileKitActionListeners(), this);
        pluginManager.registerEvents(new ClaimWallListeners(this), this);
        pluginManager.registerEvents(new EnchantmentLimiterListeners(), this);
        pluginManager.registerEvents(new PotionLimiterListeners(), this);
        pluginManager.registerEvents(new DeathLookupListeners(), this);
        pluginManager.registerEvents(new CombatLoggerListeners(this), this);
        pluginManager.registerEvents(new BlockOperationModifierListeners(), this);
        pluginManager.registerEvents(new KothCreateProcedureListeners(), this);
        pluginManager.registerEvents(new CitadelCreateProcedureListeners(), this);
        pluginManager.registerEvents(new GlowstoneCreateProcedureListeners(), this);
        pluginManager.registerEvents(new KothEventListeners(), this);
        pluginManager.registerEvents(new CitadelEventListeners(), this);
        pluginManager.registerEvents(new GlowstoneEventListeners(), this);
        //pluginManager.registerEvents(new ElevatorListeners(), this);
        pluginManager.registerEvents(new SubclaimListeners(), this);
        pluginManager.registerEvents(new CrateListeners(), this);
        pluginManager.registerEvents(new ItemDyeListeners(), this);
        pluginManager.registerEvents(new RecipeListeners(), this);
        pluginManager.registerEvents(new GlitchListeners(), this);
        pluginManager.registerEvents(new ModeListeners(), this);
        pluginManager.registerEvents(new ScoreboardListeners(), this);
        pluginManager.registerEvents(new EndListener(), this);
        pluginManager.registerEvents(new ChatListeners(), this);
        pluginManager.registerEvents(new ClaimListeners(), this);
        pluginManager.registerEvents(new KitListeners(), this);
        pluginManager.registerEvents(new BorderListener(), this);
        pluginManager.registerEvents(new SnowBallListener(), this);
        pluginManager.registerEvents(new GrapplingHookListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new EnderpearlListener(this), this);
        pluginManager.registerEvents(new EnchantingTableFix(), this);
        pluginManager.registerEvents(new BeaconStreanthFixListener(), this);
        pluginManager.registerEvents(new BlockHitFixListener(), this);
        if (isKitmapMode()) {
            pluginManager.registerEvents(new SumoListeners(), this);
        }
    }

}
