package xyz.nkomarn.harbor.provider;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.api.AFKProvider;
import xyz.nkomarn.harbor.listener.AfkListeners;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefaultAFKProvider implements AFKProvider, Listener {
    private final Harbor harbor;
    private final boolean enabled;
    private final Map<UUID, Instant> playerActivity;
    private final AfkListeners listeners;

    public DefaultAFKProvider(@NotNull Harbor harbor) {
        this.harbor = harbor;
        playerActivity = new HashMap<>();
        if (enabled = (harbor.getConfig().getBoolean("afk-detection.fallback-enabled", true))) {
            harbor.getLogger().info("Registering fallback AFK detection system.");
            listeners = new AfkListeners(this);
            harbor.getServer().getPluginManager().registerEvents(this, harbor);
        } else {
            harbor.getLogger().info("Not registering fallback AFK detection system.");
            listeners = null;
        }
    }

    @Override
    public boolean isAFK(Player player) {
        if (!enabled || !playerActivity.containsKey(player.getUniqueId())) {
            return false;
        }

        long minutes = playerActivity.get(player.getUniqueId()).until(Instant.now(), ChronoUnit.MINUTES);
        return minutes >= harbor.getConfiguration().getInteger("afk-detection.timeout");
    }

    /**
     * Sets the given player's last activity to the current timestamp.
     *
     * @param player The player to update.
     */
    public void updateActivity(@NotNull Player player) {
        playerActivity.put(player.getUniqueId(), Instant.now());
    }


    /**
     * Enables Harbor's fallback listeners for AFK detection if other AFKProviders are not present.
     */
    public void enableListeners() {
        if (enabled) {
            listeners.runTaskTimer(harbor, 1, 1);
            harbor.getServer().getPluginManager().registerEvents(listeners, harbor);
        }
    }

    /**
     * Disables Harbor's fallback listeners for AFK detection if other AFKProviders are present.
     */

    public void disableListeners() {
        if (enabled) {
            listeners.cancel();
            harbor.getLogger().info("Unregistering fallback AFK detection system.");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerActivity.remove(event.getPlayer().getUniqueId());
    }
}
