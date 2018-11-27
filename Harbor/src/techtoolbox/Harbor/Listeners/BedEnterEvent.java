package techtoolbox.Harbor.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import techtoolbox.Harbor.Main;


public class BedEnterEvent implements Listener {
	
	long nighttime = 12541; //18:30
	long daytime   = 23458; //05:29
	
	@EventHandler
	public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
		Main.bypassers.clear();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.hasPermission("harbor.bypass")) {
				Main.bypassers.add(player.getName());
			}
		}
	    
		// Player can only sleep during night
		long time = event.getPlayer().getWorld().getTime();
		
		if (nighttime < time && time < daytime) {
		
			if (!event.getPlayer().hasPermission("harbor.bypass")) {
				Main.worlds.put(event.getPlayer().getWorld(), Integer.valueOf((Main.worlds.get(event.getPlayer().getWorld())).intValue() + 1));
		      
				// Chat messages
				if (Main.plugin.getConfig().getBoolean("messages.chat.chat") && (Main.plugin.getConfig().getString("messages.chat.sleeping").length() != 0)) {
					Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("messages.chat.sleeping")
							.replace("[sleeping]", String.valueOf(Main.worlds.get(event.getPlayer().getWorld())))
							.replace("[online]", String.valueOf(event.getPlayer().getWorld().getPlayers().size() - Main.bypassers.size()))
							.replace("[player]", event.getPlayer().getName())
							.replace("[needed]", String.valueOf(Math.max(0, Math.round(event.getPlayer().getWorld().getPlayers().size() * Float.parseFloat(Main.plugin.getConfig().getString("values.percent")) - Main.bypassers.size() - (Main.worlds.get(event.getPlayer().getWorld())).intValue()))))));
				}
	
				// Skip night feature
				if (Main.plugin.getConfig().getBoolean("features.skipNight") && ((Main.worlds.get(event.getPlayer().getWorld())).intValue() >= event.getPlayer().getWorld().getPlayers().size() * Float.parseFloat(Main.plugin.getConfig().getString("values.percent")) - Main.bypassers.size()) && (event.getPlayer().getWorld().getPlayers().size() > 1)) {
					Bukkit.getServer().getWorld(event.getPlayer().getWorld().getName()).setTime(1000L);
		        
					// Weather clear feature
			        if (Main.plugin.getConfig().getBoolean("features.clearWeather")) {
			        	event.getPlayer().getWorld().setStorm(false);
			        	event.getPlayer().getWorld().setThundering(false);
			        }
			        
			        // Night skipped message
			        if (Main.plugin.getConfig().getBoolean("messages.chat.chat") && (Main.plugin.getConfig().getString("messages.chat.skipped").length() != 0)) {
			        	Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("messages.chat.skipped")));
			        }
				}
			}
		    else if (Main.plugin.getConfig().getString("messages.chat.bypass").length() != 0) {
		      event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("messages.chat.bypass")));
		    }
		}
	    
		// Reset value if below zero (plugin glitch)
		if ((Main.worlds.get(event.getPlayer().getWorld())).intValue() < 0) {
			Main.worlds.put(event.getPlayer().getWorld(), Integer.valueOf(0));
		}
	}
}
