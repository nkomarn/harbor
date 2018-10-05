package techtoolbox.Harbor.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import techtoolbox.Harbor.Main;

public class BedLeaveEvent implements Listener {
	@EventHandler
	public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
		Main.bypassers.clear();
	    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
	    	if (player.hasPermission("harbor.bypass")) {
	    		Main.bypassers.add(player.getName());
	    	}
	    }
	    
	    if (!event.getPlayer().hasPermission("harbor.bypass")) {
	    	Main.worlds.put(event.getPlayer().getWorld(), Integer.valueOf((Main.worlds.get(event.getPlayer().getWorld())).intValue() - 1));
	    }
	    
	    // Reset value if below zero (plugin glitch)
	    if ((Main.worlds.get(event.getPlayer().getWorld())).intValue() < 0) {
	    	Main.worlds.put(event.getPlayer().getWorld(), Integer.valueOf(0));
	    }
	}
}
