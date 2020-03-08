package live.ghostly.hcfactions.event.sumo;

import com.google.common.collect.Maps;
import live.ghostly.hcfactions.FactionsPlugin;
import live.ghostly.hcfactions.factions.type.SystemFaction;
import live.ghostly.hcfactions.files.ConfigFile;
import live.ghostly.hcfactions.profile.Profile;
import live.ghostly.hcfactions.util.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SumoEvent {

    @Getter
    @Setter
    private Location waitLocation;
    @Getter
    @Setter
    private Location firstSpawnLocation;
    @Getter
    @Setter
    private Location secondSpawnLocation;
    @Getter
    private Map<UUID, SumoPlayer> players = Maps.newHashMap();
    @Getter
    private WaterCheckTask waterCheckTask;
    @Getter
    private CountdownTask countdownTask;
    @Getter
    @Setter
    private boolean started = false;
    @Getter
    @Setter
    private boolean preStart = false;
    private FactionsPlugin plugin = FactionsPlugin.getInstance();
    private ConfigFile config = plugin.getMainConfig();

    public SumoEvent() {
        if (config.getString("waitLocation") != null) {
            this.waitLocation = LocationSerialization.deserializeLocation(config.getString("waitLocation"));
            Bukkit.getLogger().log(Level.WARNING, "Lobby location not found");
        }
        if (config.getString("firstSpawnLocation") != null) {
            this.firstSpawnLocation = LocationSerialization.deserializeLocation(config.getString("firstSpawnLocation"));
            Bukkit.getLogger().log(Level.WARNING, "First spawn location not found");
        }
        if (config.getString("secondSpawnLocation") != null) {
            this.secondSpawnLocation = LocationSerialization.deserializeLocation(config.getString("secondSpawnLocation"));
            Bukkit.getLogger().log(Level.WARNING, "Second spawn location not found");
        }
    }

    public static void denyMovement(Player player) {
        player.setWalkSpeed(0.0F);
        player.setFlySpeed(0.0F);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
    }

    public static void allowMovement(Player player) {
        player.setWalkSpeed(0.2f * 1.1F);
        player.setFlySpeed(0.2f * 1.1F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
    }

    public void join(Player player) {
        if (waitLocation == null) {
            player.sendMessage(ChatColor.RED + "Lobby location not found.");
            return;
        }
        if (this.players.size() >= 100) {
            player.sendMessage(Style.translate("&cSorry! The event is already full"));
            return;
        }
        if (players.containsKey(player.getUniqueId())) {
            player.sendMessage(Style.translate("&cYou are already in this event"));
            return;
        }
        this.players.put(player.getUniqueId(), new SumoPlayer(player.getUniqueId()));
        SumoPlayer sumoPlayer = getPlayer(player);
        sumoPlayer.setState(SumoPlayer.SumoPlayerState.WAITING);
        sumoPlayer.setArmor(player.getInventory().getArmorContents().clone());
        sumoPlayer.setInventory(player.getInventory().getContents().clone());
        PlayerUtil.clearPlayer(player);
        giveLobbyItems(player);
        player.teleport(waitLocation);
    }

    @EventHandler
    public void PlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (event.getItemDrop().getItemStack().getType() == Material.FIREBALL) {
            event.setCancelled(true);
        }
    }

    public void preStart(Player hoster) {
        preStart = true;
        countdownTask = new CountdownTask(hoster);
        countdownTask.runTaskTimerAsynchronously(plugin, 20L, 20L);
    }

    public void start() {
        this.waterCheckTask = new WaterCheckTask();
        this.waterCheckTask.runTaskTimer(plugin, 0, 10L);
        selectPlayers();
        started = true;
        preStart = false;
    }

    public Player selectRandomPlayer() {
        List<UUID> watingsPlayers = getByState(SumoPlayer.SumoPlayerState.WAITING);
        Collections.shuffle(watingsPlayers);
        UUID uuid = watingsPlayers.get(ThreadLocalRandom.current().nextInt(watingsPlayers.size()));

        getPlayer(uuid).setState(SumoPlayer.SumoPlayerState.PREPARING);
        return Bukkit.getPlayer(uuid);
    }

    public void selectPlayers() {

        if (this.getByState(SumoPlayer.SumoPlayerState.WAITING).size() == 1) {
            Player winner = Bukkit.getPlayer(this.getByState(SumoPlayer.SumoPlayerState.WAITING).get(0));

            String announce = ChatColor.DARK_RED + "[Event] " + ChatColor.WHITE.toString() + "Winner: " + ChatColor.RED + winner.getName();
            Bukkit.broadcastMessage(announce);

            this.players.clear();
            end();
            return;
        }

        Player player1 = selectRandomPlayer();
        Player player2 = selectRandomPlayer();

        if (player1 == null || player2 == null) {
            selectPlayers();
            return;
        }

        sendMessage("&c[Event] &f" + "Selecting random players...");

        SumoPlayer picked1Data = getPlayer(player1);
        SumoPlayer picked2Data = getPlayer(player2);

        picked1Data.setFighting(picked2Data);
        picked2Data.setFighting(picked1Data);

        PlayerUtil.clearPlayer(player1);
        PlayerUtil.clearPlayer(player2);

        if (firstSpawnLocation == null) {
            sendMessage(ChatColor.RED + "First location not found");
            end();
            return;
        }
        if (secondSpawnLocation == null) {
            sendMessage(ChatColor.RED + "Second location not found");
            end();
            return;
        }

        player1.teleport(firstSpawnLocation);
        player2.teleport(secondSpawnLocation);
        denyMovement(player1);
        denyMovement(player2);

        sendMessage("&cStarting event match. &f" + "(&c" + player1.getName() + " &fvs&c " + player2.getName() + "&f)");

        BukkitTask task = new SumoFightTask(player1, player2, picked1Data, picked2Data).runTaskTimer(plugin, 0, 20);

        picked1Data.setFightTask(task);
        picked2Data.setFightTask(task);
    }

    public void leave(Player player, boolean disconnect) {
        if (this.onDeath() != null) {
            this.onDeath().accept(player);
        }

        PlayerUtil.clearPlayer(player);

        SumoPlayer sumoPlayer = getPlayer(player);
        if (!disconnect) {
            PlayerUtil.clearPlayer(player);
            player.getInventory().setArmorContents(sumoPlayer.getArmor());
            player.getInventory().setContents(sumoPlayer.getInventory());
        }

        SystemFaction spawn = SystemFaction.getByName("Spawn");

        if (spawn == null || spawn.getHome() == null) {
            player.teleport(player.getWorld().getSpawnLocation());
            return;
        }
        player.teleport(LocationSerialization.deserializeLocation(spawn.getHome()));
        this.players.remove(player.getUniqueId());
        if (disconnect) {
            Profile.getByPlayer(player).setTeleport(true);
        }
    }

    public Consumer<Player> onDeath() {

        return player -> {

            SumoPlayer data = getPlayer(player);

            if (data == null || data.getFighting() == null) {
                return;
            }

            if (data.getState() == SumoPlayer.SumoPlayerState.FIGHTING || data.getState() == SumoPlayer.SumoPlayerState.PREPARING) {

                SumoPlayer killerData = data.getFighting();
                Player killer = plugin.getServer().getPlayer(killerData.getUuid());

                data.getFightTask().cancel();
                killerData.getFightTask().cancel();

                data.setState(SumoPlayer.SumoPlayerState.ELIMINATED);
                killerData.setState(SumoPlayer.SumoPlayerState.WAITING);

                PlayerUtil.clearPlayer(player);
                giveLobbyItems(player);

                PlayerUtil.clearPlayer(killer);
                giveLobbyItems(killer);

                player.teleport(waitLocation);
                killer.teleport(waitLocation);

                sendMessage(ChatColor.DARK_RED + "[Event] " + ChatColor.RED + player.getName() + ChatColor.WHITE + " has been eliminated" + (killer == null ? "." : " by " + ChatColor.GREEN + killer.getName()));

                leave(player, false);

                if (this.getByState(SumoPlayer.SumoPlayerState.WAITING).size() == 1) {
                    Player winner = Bukkit.getPlayer(this.getByState(SumoPlayer.SumoPlayerState.WAITING).get(0));

                    for (int i = 0; i <= 2; i++) {
                        String announce = ChatColor.DARK_RED + "[Event] " + ChatColor.WHITE.toString() + "Winner: " + winner.getName();
                        Bukkit.broadcastMessage(announce);
                    }
                    end();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate key koth 1 " + winner.getName());
                            winner.sendMessage(ChatColor.GREEN + "You have received (1) Crate Key.");
                        }
                    }.runTaskLater(FactionsPlugin.getInstance(), 10L);
                } else {
                    plugin.getServer().getScheduler().runTaskLater(plugin, this::selectPlayers, 3 * 20);
                }
            }
        };
    }

    public void sendMessage(String msg) {
        for (SumoPlayer sumoPlayer : players.values()) {
            Player player = Bukkit.getPlayer(sumoPlayer.getUuid());
            if (player == null) {
                players.remove(sumoPlayer.getUuid());
                continue;
            }
            player.sendMessage(Style.translate(msg));
        }
    }

    public List<UUID> getByState(SumoPlayer.SumoPlayerState state) {
        return players.values().stream().filter(player -> player.getState() == state).map(SumoPlayer::getUuid).collect(Collectors.toList());
    }

    public void giveLobbyItems(Player player) {
        player.getInventory().setItem(9, new ItemBuilder(Material.FIREBALL).name(Style.translate("&cLeave")).build());
    }

    public void end() {
        if (waterCheckTask != null) {
            waterCheckTask.cancel();
        }
        this.players.values().forEach(sumoPlayer -> {
            Player player = Bukkit.getPlayer(sumoPlayer.getUuid());
            PlayerUtil.clearPlayer(player);

            player.getInventory().setArmorContents(sumoPlayer.getArmor());
            player.getInventory().setContents(sumoPlayer.getInventory());

            SystemFaction spawn = SystemFaction.getByName("Spawn");

            if (spawn == null || spawn.getHome() == null) {
                player.teleport(player.getWorld().getSpawnLocation());
                return;
            }

            player.teleport(LocationSerialization.deserializeLocation(spawn.getHome()));
        });
        this.players.clear();
        started = false;
        preStart = false;
    }

    public SumoPlayer getPlayer(Player player) {
        return this.players.get(player.getUniqueId());
    }

    public SumoPlayer getPlayer(UUID uuid) {
        return this.players.get(uuid);
    }

    @Getter
    @RequiredArgsConstructor
    public class SumoFightTask extends BukkitRunnable {
        private final Player player;
        private final Player other;

        private final SumoPlayer playerSumo;
        private final SumoPlayer otherSumo;

        private int time = 90;

        @Override
        public void run() {

            if (player == null || other == null || !player.isOnline() || !other.isOnline()) {
                cancel();
                return;
            }

            if (time == 90) {
                PlayerUtil.sendMessage(ChatColor.RED + "The match starts in " + ChatColor.WHITE + 3 + ChatColor.RED + "...", player, other);
            } else if (time == 89) {
                PlayerUtil.sendMessage(ChatColor.RED + "The match starts in " + ChatColor.WHITE + 2 + ChatColor.RED + "...", player, other);
            } else if (time == 88) {
                PlayerUtil.sendMessage(ChatColor.RED + "The match starts in " + ChatColor.WHITE + 1 + ChatColor.RED + "...", player, other);
            } else if (time == 87) {
                PlayerUtil.sendMessage(ChatColor.RED + "The match has started, good luck!", player, other);
                this.otherSumo.setState(SumoPlayer.SumoPlayerState.FIGHTING);
                this.playerSumo.setState(SumoPlayer.SumoPlayerState.FIGHTING);
                allowMovement(player);
                allowMovement(other);
            } else if (time <= 0) {
                List<Player> players = Arrays.asList(player, other);
                Player winner = players.get(ThreadLocalRandom.current().nextInt(players.size()));
                players.stream().filter(pl -> !pl.equals(winner)).forEach(pl -> onDeath().accept(pl));
                cancel();
                return;
            }

            if (Arrays.asList(30, 25, 20, 15, 10).contains(time)) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The match ends in " + ChatColor.GREEN + time + ChatColor.YELLOW + "...", player, other);
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(time)) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The match is ending in " + ChatColor.GREEN + time + ChatColor.YELLOW + "...", player, other);
            }

            time--;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public class CountdownTask extends BukkitRunnable {
        private Player hoster;
        @Getter
        private int timeUntilStart = 60;

        public CountdownTask(Player hoster) {
            this.hoster = hoster;
        }

        @Override
        public void run() {
            if (this.timeUntilStart <= 0) {

                if (this.canStart()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            start();
                        }
                    }.runTask(plugin);
                } else {
                    plugin.getServer().getScheduler().runTask(plugin, this::onCancel);
                }
                cancel();
                return;
            }
            if (this.shouldAnnounce(this.timeUntilStart)) {

                String toSend = Style.translate("&c&lSumo &fhosted by &c" + hoster.getName() + "  &fis starting..." +
                        " &7(&a" + players.size() + "&7/&a100&7) &b!Click to join!");

                Clickable message = new Clickable(toSend,
                        Style.RED + "Click to join this event.",
                        "/join sumo");
                Bukkit.getServer().getOnlinePlayers().stream().filter(other -> !players.containsKey(other.getUniqueId())).forEach(player -> {
                    player.sendMessage(" ");
                    message.sendToPlayer(player);
                    player.sendMessage(" ");
                });
            }
            --this.timeUntilStart;
        }

        private boolean shouldAnnounce(int timeUntilStart) {
            return Arrays.asList(45, 30, 15, 10, 5).contains(timeUntilStart);
        }

        private boolean canStart() {
            return players.size() >= 4;
        }

        private void onCancel() {
            preStart = false;
            sendMessage(ChatColor.RED + "Not enough players. Event has been cancelled");
            end();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public class WaterCheckTask extends BukkitRunnable {
        @Override
        public void run() {

            if (getByState(SumoPlayer.SumoPlayerState.FIGHTING).size() <= 1) {
                return;
            }

            getByState(SumoPlayer.SumoPlayerState.FIGHTING).forEach(sumoPlayer -> {

                Player player = Bukkit.getPlayer(sumoPlayer);

                Block legs = player.getLocation().getBlock();
                Block head = legs.getRelative(BlockFace.UP);
                if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                    onDeath().accept(player);
                }
            });
        }
    }

}
