package xyz.nkomarn.harbor.provider;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.api.AFKProvider;
import xyz.nkomarn.harbor.listener.AfkListener;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * The default AFK provider, which should be disabled if any others are registered
 */
public final class DefaultAFKProvider implements AFKProvider, Listener {
    private final boolean enabled;
    private Map<UUID, Instant> playerActivity;
    private final AfkListener listener;
    private final int timeout;
    private final Harbor harbor;

    public DefaultAFKProvider(@NotNull Harbor harbor) {
        this.harbor = harbor;
        if (enabled = (harbor.getConfig().getBoolean("afk-detection.fallback-enabled", true))) {
            timeout = harbor.getConfiguration().getInteger("afk-detection.timeout");
            listener = new AfkListener(this);
            enableListeners();
        } else {
            harbor.getLogger().info("Not registering fallback AFK detection system.");
            listener = null;
            timeout = -1;
        }
    }

    @Override
    public boolean isAFK(Player player) {
        if (!enabled || !playerActivity.containsKey(player.getUniqueId())) {
            return false;
        }

        long minutes = playerActivity.get(player.getUniqueId()).until(Instant.now(), ChronoUnit.MINUTES);
        return minutes >= timeout;
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
            harbor.getLogger().log(Level.FINE, "Enabling listeners for Default AFK Provider");
            playerActivity = new HashMap<>();
            listener.start();
        }
    }

    /**
     * Disables Harbor's fallback listeners for AFK detection if other AFKProviders are present.
     */
    public void disableListeners() {
        if (enabled) {
            harbor.getLogger().log(Level.FINE, "Disabling listeners for Default AFK Provider");
            listener.stop();
            playerActivity = null;
        }
    }


    public void removePlayer(UUID uniqueId) {
        playerActivity.remove(uniqueId);
    }

    @NotNull
    public Harbor getHarbor() {
        return harbor;
    }
}
