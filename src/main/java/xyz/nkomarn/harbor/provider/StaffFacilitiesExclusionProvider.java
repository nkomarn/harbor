package xyz.nkomarn.harbor.provider;

import me.xtomyserrax.StaffFacilities.SFAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.api.ExclusionProvider;

/**
 * Allows Staff Members to be excluded in certain conditions. Can be used as an example
 * implementation for {@link ExclusionProvider}
 */
public class StaffFacilitiesExclusionProvider implements ExclusionProvider {
    private final Harbor harbor;
    private final boolean enabled;

    public StaffFacilitiesExclusionProvider(Harbor harbor) {
        this.harbor = harbor;
        ConfigurationSection exclusions = harbor.getConfig().getConfigurationSection("exclusions");
        enabled = exclusions != null &&
                exclusions.getBoolean("exclude-active-staff") &&
                harbor.getServer().getPluginManager().getPlugin("StaffFacilities") != null;
    }

    @Override
    public boolean isExcluded(Player player) {
        if (!enabled)
            return false;
        ConfigurationSection exclusions = harbor.getConfig().getConfigurationSection("exclusions");
        // Suppress Constant Conditions checking due to the way SFAPI is implemented in maven
        //noinspection ConstantConditions
        return SFAPI.isPlayerOnDuty(player) ||
                SFAPI.isPlayerOnWatchover(player) ||
                SFAPI.isPlayerOnStaffwatch(player) ||
                SFAPI.isPlayerFakeleaved(player) ||
                ((exclusions != null && exclusions.getBoolean("exclude-vanished")) && (
                        SFAPI.isPlayerVanished(player) ||
                                SFAPI.isPlayerStaffVanished(player)));

    }
}
