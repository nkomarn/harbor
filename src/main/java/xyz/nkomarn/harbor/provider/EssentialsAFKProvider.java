package xyz.nkomarn.harbor.provider;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
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
    private final Harbor harbor;

    public EssentialsAFKProvider(@NotNull Harbor harbor) {
        this.harbor = harbor;
    }

    @Override
    public boolean isAFK(Player player) {
        Optional<Essentials> essentials = harbor.getEssentials();
        if (essentials.isPresent()) {
            User user = essentials.get().getUser(player);

            if (user != null) {
                return user.isAfk();
            }
        }
        return false;
    }
}
