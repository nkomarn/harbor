package xyz.nkomarn.harbor.provider;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.api.ExclusionProvider;

/**
 * A class-base {@link ExclusionProvider} which handles exclusions based on {@link org.bukkit.GameMode}
 */
public final class GameModeExclusionProvider implements ExclusionProvider {
    private final Harbor harbor;

    public GameModeExclusionProvider(@NotNull Harbor harbor) {
        this.harbor = harbor;
    }

    @Override
    public boolean isExcluded(Player player) {
        return harbor.getConfig().getBoolean("exclusions.exclude-" + player.getGameMode().toString().toLowerCase(), false);
    }
}
