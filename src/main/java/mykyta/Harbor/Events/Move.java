package mykyta.Harbor.Events;

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