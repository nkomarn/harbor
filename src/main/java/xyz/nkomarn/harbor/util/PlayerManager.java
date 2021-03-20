package xyz.nkomarn.harbor.util;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.listener.AfkListeners;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerManager implements Listener {

    private final Harbor harbor;
    private final Map<UUID, Instant> cooldowns;
    private final Map<UUID, Instant> playerActivity;

    public PlayerManager(@NotNull Harbor harbor) {
        this.harbor = harbor;
        this.cooldowns = new HashMap<>();
        this.playerActivity = new HashMap<>();
    }

    /**
     * Gets the last tracked cooldown time for a given player.
     *
     * @param player The player for which to return cooldown time.
     *
     * @return The player's last cooldown time.
     */
    public Instant getCooldown(@NotNull Player player) {
        return cooldowns.getOrDefault(player.getUniqueId(), Instant.MIN);
    }

    /**
     * Sets a player's cooldown to a specific, fixed value.
     *
     * @param player   The player for which to set cooldown.
     * @param cooldown The cooldown value.
     */
    public void setCooldown(@NotNull Player player, Instant cooldown) {
        cooldowns.put(player.getUniqueId(), cooldown);
    }

    /**
     * Resets every players' message cooldown.
     */
    public void clearCooldowns() {
        cooldowns.clear();
    }

    /**
     * Checks if a player is considered "AFK" for Harbor's player checks.
     *
     * @param player The player to check.
     *
     * @return Whether the player is considered AFK.
     */
    public boolean isAfk(@NotNull Player player) {
        if (!harbor.getConfiguration().getBoolean("afk-detection.enabled")) {
            return false;
        }

        Optional<Essentials> essentials = harbor.getEssentials();
        if (essentials.isPresent()) {
            User user = essentials.get().getUser(player);

            if (user != null) {
                return user.isAfk();
            }
        }

        if (!playerActivity.containsKey(player.getUniqueId())) {
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
     * Registers Harbor's fallback listeners for AFK detection if Essentials is not present.
     */
    public void registerFallbackListeners() {
        AfkListeners afkListeners = new AfkListeners(this);
        afkListeners.runTaskTimer(harbor, 1, 1);
        harbor.getServer().getPluginManager().registerEvents(afkListeners, harbor);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        cooldowns.remove(uuid);
        playerActivity.remove(uuid);
    }
}
