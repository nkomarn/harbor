package xyz.nkomarn.harbor.util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.api.AFKProvider;
import xyz.nkomarn.harbor.api.LogicType;
import xyz.nkomarn.harbor.provider.DefaultAFKProvider;
import xyz.nkomarn.harbor.provider.EssentialsAFKProvider;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerManager implements Listener {
    private final Map<UUID, Instant> cooldowns;
    private final Set<AFKProvider> andedProviders;
    private final Set<AFKProvider> oredProviders;
    private final DefaultAFKProvider defaultProvider;

    public PlayerManager(@NotNull Harbor harbor) {
        this.cooldowns = new HashMap<>();
        this.andedProviders = new HashSet<>();
        this.oredProviders = new HashSet<>();
        this.defaultProvider = new DefaultAFKProvider();

        updateListeners();
        if (harbor.getConfig().getBoolean("afk-detection.essentials-enabled", false)) {
            if (harbor.getEssentials().isPresent()) {
                addAfkProvider(new EssentialsAFKProvider(harbor.getEssentials().get()),
                        LogicType.fromConfig(harbor.getConfig(), "essentials-detection-mode", LogicType.AND));
            } else {
                harbor.getLogger().info("Essentials not present- skipping registering Essentials AFK detection");
            }
        }
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
        // If there are no providers registered, we go to the default provider
        if(oredProviders.isEmpty() && andedProviders.isEmpty()){
            return defaultProvider.isAFK(player);
        }
        return oredProviders.stream().anyMatch(provider -> provider.isAFK(player)) ||
                (!andedProviders.isEmpty() && andedProviders.stream().allMatch(provider -> provider.isAFK(player)));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        cooldowns.remove(uuid);
    }


    /**
     * Add an AFK Provider to harbor, so an external plugin can provide an AFK status to harbor
     *
     * @param provider  The {@link AFKProvider} to be added
     * @param logicType The type of logic (And or Or, {@link LogicType}) to be used with the given provider
     */
    public void addAfkProvider(AFKProvider provider, LogicType logicType) {
        (logicType == LogicType.AND ? andedProviders : oredProviders).add(provider);
        updateListeners();
    }

    /**
     * Remove an AFK provider from Harbor, provided for external plugins.
     * @param provider the {@link AFKProvider} to be removed.
     */
    public void removeAfkProvider(AFKProvider provider) {
        andedProviders.remove(provider);
        oredProviders.remove(provider);
        updateListeners();
    }

    private void updateListeners() {
        if (andedProviders.isEmpty() && oredProviders.isEmpty()) {
            defaultProvider.enableListeners();
        } else {
            defaultProvider.disableListeners();
        }
    }
}
