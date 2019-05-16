package mykyta.Harbor.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import mykyta.Harbor.Util;

public class CommandEvent implements Listener {
    Util util = new Util();

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        util.updateActivity(event.getPlayer());    
    }
}