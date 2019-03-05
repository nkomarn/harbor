package mykyta.Harbor.Events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import mykyta.Harbor.Harbor;

public class BedEnter implements Listener {
    
    Harbor harbor;
    public BedEnter(Harbor instance) {
        harbor = instance;
    }

    @EventHandler(priority = EventPriority.HIGH)
	public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
        // Prevent bed entry if "blockSleep" is enabled in the config
		if (harbor.getConfig().getBoolean("features.blockSleep")) {
			if (harbor.getConfig().getString("messages.chat.blocked").length() > 0) {
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', harbor.getConfig().getString("messages.chat.blocked")));	
			}
			//Main.sendActionbar("sleepingBlocked", null, event.getPlayer())
            event.setCancelled(true);	
            return;
        }
        
        
    }
}