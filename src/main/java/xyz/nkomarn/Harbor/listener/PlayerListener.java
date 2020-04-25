package xyz.nkomarn.Harbor.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.nkomarn.Harbor.util.Config;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("harbor.admin")) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "%sYour Harbor configuration is outdated- please regenerate it or Harbor may not work properly.",
                    Config.getString("messages.miscellaneous.prefix") // Use old prefix location
            )));
        }
    }
}
