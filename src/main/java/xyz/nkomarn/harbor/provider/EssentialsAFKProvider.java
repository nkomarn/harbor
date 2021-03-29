package xyz.nkomarn.harbor.provider;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.api.AFKProvider;

/**
 * An {@link AFKProvider} that uses Essentials; can be used as an example of how external
 * plugins can implement an {@link AFKProvider}
 */
public final class EssentialsAFKProvider implements AFKProvider {
    private final Essentials essentials;

    public EssentialsAFKProvider(@NotNull Essentials essentials) {
        this.essentials = essentials;
    }

    @Override
    public boolean isAFK(Player player) {
        User user = essentials.getUser(player);
        return user != null && user.isAfk();
    }
}
