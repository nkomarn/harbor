package xyz.nkomarn.harbor.api;

import org.bukkit.entity.Player;

/**
 * The {@link AFKProvider} interface provides a way for an implementing
 * class in an external plugin to provide a way for external plugins to tell
 * Harbor if a Player is AFK, in case of a custom AFK implementation
 *
 * @see xyz.nkomarn.harbor.Harbor#addExclusionProvider(ExclusionProvider)
 */
public interface AFKProvider {
    /**
     * Tests if a {@link Player} is AFK
     *
     * @param player The {@link Player} that is being checked
     *
     * @return If the player is afk (true) or not (false)
     */
    boolean isAFK(Player player);
}
