package xyz.nkomarn.Harbor.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.nkomarn.Harbor.util.Afk;

public class AfkListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Afk.updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Afk.updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Afk.updateActivity(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Afk.updateActivity((Player) event.getWhoClicked());
    }
}
