package xyz.nkomarn.harbor.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nkomarn.harbor.provider.DefaultAFKProvider;
import xyz.nkomarn.harbor.util.PlayerManager;

import java.util.ArrayDeque;
import java.util.Queue;

public final class AfkListeners extends BukkitRunnable implements Listener {
    private final DefaultAFKProvider afkProvider;
    private double checksToMake = 0;
    private final Queue<AfkPlayer> players = new ArrayDeque<>();

    public AfkListeners(DefaultAFKProvider afkProvider) {
        this.afkProvider = afkProvider;
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
        players.add(new AfkPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        players.remove(new AfkPlayer(event.getPlayer()));
    }

    @Override
    public void run() {
        if (players.isEmpty()) return;

        // We want every player to get a check every 20 ticks.
        checksToMake += players.size() / 20.0;
        long start = System.currentTimeMillis();

        // We assume that a tick should take 50 ms at max.
        // To leave some space for other plugins we only take 20 ms of a tick.
        while (System.currentTimeMillis() - start < 20 && checksToMake > 0) {
            AfkPlayer afkPlayer = players.poll();
            if (afkPlayer.changed()) {
                afkProvider.updateActivity(afkPlayer.player);
            }
            players.add(afkPlayer);
            checksToMake--;
        }
    }

    @Override
    public void cancel() {
        super.cancel();
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
        private boolean changed() {
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
