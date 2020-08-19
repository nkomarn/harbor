package xyz.nkomarn.harbor.util;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerManager implements Listener {

    private final Harbor harbor;
    private final Object2LongMap<UUID> cooldowns;
    private final Object2LongMap<UUID> playerActivity;

    public PlayerManager(@NotNull Harbor harbor) {
        this.harbor = harbor;
        this.cooldowns = new Object2LongOpenHashMap<>();
        this.playerActivity = new Object2LongOpenHashMap<>();
    }

    /**
     * Gets the last tracked cooldown time for a given player.
     * @param player The player for which to return cooldown time.
     * @return The player's last cooldown time.
     */
    public long getCooldown(@NotNull Player player) {
        return cooldowns.getOrDefault(player.getUniqueId(), 0);
    }

    // TODO javadoc
    public void setCooldown(@NotNull Player player, long cooldown) {
        cooldowns.put(player.getUniqueId(), cooldown);
    }

    // TODO javadocs
    public void clearCooldowns() {
        cooldowns.clear();
    }

    /**
     * Checks if a player is considered "AFK" for Harbor's player checks.
     *
     * @param player The player to check.
     * @return Whether the player is considered AFK.
     */
    public boolean isAfk(@NotNull Player player) {
        if (!harbor.getConfiguration().getBoolean("afk-detection.enabled")) {
            return false;
        }

        Essentials essentials = harbor.getEssentials();
        if (essentials != null) {
            User user = essentials.getUser(player);

            if (user != null) {
                return user.isAfk();
            }
        }

        if (!playerActivity.containsKey(player.getUniqueId())) {
            return false;
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - playerActivity.getLong(player));
        return minutes >= harbor.getConfiguration().getInteger("afk-detection.timeout");
    }

    /**
     * Sets the given player's last activity to the current timestamp.
     *
     * @param player The player to update.
     */
    public void updateActivity(@NotNull Player player) {
        playerActivity.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        cooldowns.removeLong(uuid);
        playerActivity.removeLong(uuid);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        updateActivity((Player) event.getWhoClicked());
    }
}
