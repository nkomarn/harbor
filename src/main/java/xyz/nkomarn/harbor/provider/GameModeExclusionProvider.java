package xyz.nkomarn.harbor.provider;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.api.ExclusionProvider;

import java.util.HashMap;
import java.util.Map;

public class GameModeExclusionProvider extends HarborProvider implements ExclusionProvider {
    private final Map<GameMode, Boolean> exclusionMap;
    public GameModeExclusionProvider(@NotNull Harbor harbor) {
        super(harbor);
        ConfigurationSection exclusions = harbor.getConfig().getConfigurationSection("exclusions");
        if(exclusions != null) {
            exclusionMap = new HashMap<>();
            exclusionMap.put(GameMode.ADVENTURE, exclusions.getBoolean("exclude-adventure", false));
            exclusionMap.put(GameMode.CREATIVE, exclusions.getBoolean("exclude-creative", false));
            exclusionMap.put(GameMode.SURVIVAL, exclusions.getBoolean("exclude-survival", false));
            exclusionMap.put(GameMode.SPECTATOR, exclusions.getBoolean("exclude-spectator", false));
        } else {
            exclusionMap = null;
        }
    }

    @Override
    public boolean isExcluded(Player player) {
        return exclusionMap != null ? exclusionMap.get(player.getGameMode()) : false;
    }
}
