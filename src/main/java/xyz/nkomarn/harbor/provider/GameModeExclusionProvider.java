package xyz.nkomarn.harbor.provider;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.api.ExclusionProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * A class-based exclusion provider example, which handles exclusions based on gamemode
 */
public final class GameModeExclusionProvider implements ExclusionProvider {
    private final Map<GameMode, Boolean> exclusionMap;

    public GameModeExclusionProvider() {
        ConfigurationSection exclusions = JavaPlugin.getPlugin(Harbor.class).getConfig().getConfigurationSection("exclusions");
        if (exclusions != null) {
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
        return exclusionMap != null ? exclusionMap.getOrDefault(player.getGameMode(), false) : false;
    }
}
