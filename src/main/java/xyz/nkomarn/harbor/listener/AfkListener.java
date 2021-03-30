package xyz.nkomarn.harbor.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
    private Queue<AfkPlayer> playerQueue;
    private PlayerMovementChecker movementChecker;

    // We assume that a tick should take 50 ms at max.
    // To leave some space for other plugins we only take 20 ms of a tick.
    private static final long MAX_PROCESS_TIME_MS = 20L;

    public AfkListener(DefaultAFKProvider afkProvider) {
        this.afkProvider = afkProvider;
        JavaPlugin.getPlugin(Harbor.class).getLogger().info("Registering fallback AFK detection system.");
    }

    /**
     * Provides a way to start the listener
     */
    public void start() {
        JavaPlugin plugin = JavaPlugin.getPlugin(Harbor.class);
        playerQueue = new ArrayDeque<>();
        movementChecker = new PlayerMovementChecker();

        playerQueue.addAll(Bukkit.getOnlinePlayers().stream().map((Function<Player, AfkPlayer>) AfkPlayer::new).collect(Collectors.toSet()));

        // Register listeners after populating the queue
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        // We want every player to get a check every 20 ticks.
        movementChecker.runTaskTimer(plugin, 20, 20);

        JavaPlugin.getPlugin(Harbor.class).getLogger().info("Fallback AFK detection system is enabled");
    }

    /**
     * Provides a way to halt the listener
     */
    public void stop() {
        movementChecker.cancel();
        HandlerList.unregisterAll(this);
        playerQueue = null;
        JavaPlugin.getPlugin(Harbor.class).getLogger().info("Fallback AFK detection system is disabled");
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        afkProvider.updateActivity(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        afkProvider.updateActivity(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        afkProvider.updateActivity((Player) event.getWhoClicked());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerQueue.add(new AfkPlayer(event.getPlayer()));
        afkProvider.updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        playerQueue.remove(new AfkPlayer(event.getPlayer()));
        afkProvider.removePlayer(event.getPlayer().getUniqueId());
    }

    private class PlayerMovementChecker extends BukkitRunnable {
        @Override
        public synchronized void run() {
            // Short circuit exit if there aren't any players to check
            if (playerQueue.isEmpty())
                return;


            AfkPlayer firstPlayer = playerQueue.peek();
            AfkPlayer player = firstPlayer;

            long start = System.currentTimeMillis();
            do {
                if (player.changed()) {
                    afkProvider.updateActivity(player.player);
                }
                playerQueue.add(player);
            } while (System.currentTimeMillis() - start < MAX_PROCESS_TIME_MS &&
                    !playerQueue.isEmpty() &&
                    !(player = playerQueue.poll()).equals(firstPlayer));
        }
    }

    private static class AfkPlayer {
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
        private synchronized boolean changed() {

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
