package xyz.nkomarn.harbor.provider;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.api.AFKProvider;

import java.util.Optional;

/**
 * An {@link AFKProvider} that uses Essentials; can be used as an example of how external
 * plugins can implement an {@link AFKProvider}
 */
public class EssentialsAFKProvider implements AFKProvider {
    private final Essentials essentials;

    private EssentialsAFKProvider(@NotNull Essentials essentials) {
        this.essentials = essentials;
    }


    @Override
    public boolean isAFK(Player player) {
        User user = essentials.getUser(player);
        return user != null && user.isAfk();
    }

    public static void registerEssentials(Harbor harbor) {
        ConfigurationSection afk = harbor.getConfig().getConfigurationSection("afk-detection");
        if(afk != null && afk.getBoolean("essentials-enabled")) {
            if(harbor.getEssentials().isPresent()) {
                EssentialsAFKProvider provider = new EssentialsAFKProvider(harbor.getEssentials().get());
            } else {
                harbor.getLogger().info("Essentials not present- skipping registering Essentials AFK detection");
            }
        }

    }
}
