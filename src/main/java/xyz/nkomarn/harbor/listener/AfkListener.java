package xyz.nkomarn.harbor.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.provider.DefaultAFKProvider;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class AfkListener implements Listener {
    private final DefaultAFKProvider afkProvider;
    private Queue<AfkPlayer> players;
    private PlayerMovementChecker movementChecker;

    public AfkListener(DefaultAFKProvider afkProvider) {
        this.afkProvider = afkProvider;
        JavaPlugin.getPlugin(Harbor.class).getLogger().info("Initializing fallback AFK detection system. Fallback AFK system is not enabled at this time");
    }

    /**
     * Provides a way to start the listener
     */
    public void start() {
        JavaPlugin plugin = JavaPlugin.getPlugin(Harbor.class);
        players = new ArrayDeque<>();
        movementChecker = new PlayerMovementChecker();

        // Populate the queue with any existing players
        players.addAll(Bukkit.getOnlinePlayers().stream().map((Function<Player, AfkPlayer>) AfkPlayer::new).collect(Collectors.toSet()));

        // Register listeners after populating the queue
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        // We want every player to get a check every 20 ticks. The runnable smooths out checking a certain
        // percentage of players over all 20 ticks. Thusly, the runnable must run on every tick
        movementChecker.runTaskTimer(plugin, 0, 1);

        JavaPlugin.getPlugin(Harbor.class).getLogger().info("Fallback AFK detection system is enabled");
    }

    /**
     * Provides a way to halt the listener
     */
    public void stop() {
        movementChecker.cancel();
        HandlerList.unregisterAll(this);
        players = null;
        JavaPlugin.getPlugin(Harbor.class).getLogger().info("Fallback AFK detection system is disabled");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        afkProvider.updateActivity(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        afkProvider.updateActivity(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        afkProvider.updateActivity((Player) event.getWhoClicked());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        players.add(new AfkPlayer(event.getPlayer()));
        afkProvider.updateActivity(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event) {
        players.remove(new AfkPlayer(event.getPlayer()));
        afkProvider.removePlayer(event.getPlayer().getUniqueId());
    }

    /**
     * Internal class for handling the task of checking player movement; Is a separate task so that we can cancel and restart it easily
     */
    private final class PlayerMovementChecker extends BukkitRunnable {
        private double checksToMake = 0;
        @Override
        public void run() {
            if(players.isEmpty()){
                checksToMake = 0;
                return;
            }

            // We want every player to get a check every 20 ticks. Therefore we check 1/20th of the players
            for (checksToMake += players.size() / 20.0D; checksToMake > 0 && !players.isEmpty(); checksToMake--) {
                AfkPlayer afkPlayer = players.poll();
                if (afkPlayer.changed()) {
                    afkProvider.updateActivity(afkPlayer.player);
                }
                players.add(afkPlayer);
            }
        }
    }



    private static final class AfkPlayer {
        private final Player player;
        private int hash;

        public AfkPlayer(Player player) {
            this.player = player;
        }

        /**
         * Check if the player changed its position since the last check
         *
         * @return true if the position changed
         */
        boolean changed() {
            int hash = player.getLocation().hashCode();
            boolean changed = hash != this.hash;
            this.hash = hash;
            return changed;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AfkPlayer afkPlayer = (AfkPlayer) o;
            return player.getUniqueId().equals(afkPlayer.player.getUniqueId());
        }

        @Override
        public int hashCode() {
            return player.getUniqueId().hashCode();
        }
    }
}
