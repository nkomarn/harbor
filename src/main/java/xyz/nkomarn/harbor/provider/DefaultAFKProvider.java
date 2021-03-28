package xyz.nkomarn.harbor.provider;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.api.AFKProvider;

public class DefaultAFKProvider implements AFKProvider, Listener {
    private final Harbor harbor;

    public DefaultAFKProvider(@NotNull Harbor harbor) {
        this.harbor = harbor;
    }

    @Override
    public boolean isAFK(Player player) {
        return false;
    }
}
