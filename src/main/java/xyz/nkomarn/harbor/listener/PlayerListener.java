package xyz.nkomarn.harbor.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.util.Config;

public class PlayerListener implements Listener {

    private final Harbor harbor;

    public PlayerListener(@NotNull Harbor harbor) {
        this.harbor = harbor;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("harbor.admin")) {
            return;
        }

        if (harbor.getConfiguration().getString("version").equals("1.6.2")) {
            return;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', harbor.getConfiguration().getString("messages.miscellaneous.prefix") +
                "Your Harbor configuration is outdated- please regenerate it or Harbor may not work properly."));
    }
}
