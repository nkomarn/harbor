package techtoolbox.Harbor.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import techtoolbox.Harbor.Main;

public class BedLeaveEvent implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
		Main.bypassers.clear();
		if (Main.plugin.getConfig().getBoolean("features.bypass")) {
		    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
		    	if (player.hasPermission("harbor.bypass")) {
		    		Main.bypassers.add(player.getName());
		    	}
		    }
		}
	    
	    if (!event.getPlayer().hasPermission("harbor.bypass")) {
	    	Main.worlds.put(event.getPlayer().getWorld(), Integer.valueOf((Main.worlds.get(event.getPlayer().getWorld())).intValue() - 1));
	    } 
	}
}
