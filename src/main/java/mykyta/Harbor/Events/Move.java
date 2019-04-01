package mykyta.Harbor.Events;

import java.time.Instant;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import mykyta.Harbor.Util;

public class Move implements Listener {
    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent event) {
        Util.activity.put(event.getPlayer(), System.currentTimeMillis());
    }
}