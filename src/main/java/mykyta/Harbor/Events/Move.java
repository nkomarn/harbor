package mykyta.Harbor.Events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import mykyta.Harbor.Util;

public class Move implements Listener {
    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent event) {
        Util util = new Util();
        util.updateActivity(event.getPlayer());
    }
}