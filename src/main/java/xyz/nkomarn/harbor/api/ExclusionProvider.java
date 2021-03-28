package xyz.nkomarn.harbor.api;

import org.bukkit.entity.Player;

/**
 * The {@link ExclusionProvider} interface provides a way for an implementing
 * class in an external plugin to provide a way for external plugins to control
 * programmatically if a player should be excluded from the cap
 *
 * @see xyz.nkomarn.harbor.Harbor#addExclusionProvider(ExclusionProvider)
 */
public interface ExclusionProvider {
    /**
     * Tests if a {@link Player} is excluded from the sleep checks for Harbor
     *
     * @param player The {@link Player} that is being checked
     *
     * @return If the player is excluded (true) or not (false)
     */
    boolean isExcluded(Player player);
}
