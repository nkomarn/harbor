package mykyta.Harbor.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import mykyta.Harbor.Util;

public class ChatEvent implements Listener {
    Util util = new Util();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        util.updateActivity(event.getPlayer());
    }
}