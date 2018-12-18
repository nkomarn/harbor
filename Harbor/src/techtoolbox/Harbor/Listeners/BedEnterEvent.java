package techtoolbox.Harbor.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;

import techtoolbox.Harbor.Main;

public class BedEnterEvent implements Listener {
	
	String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
		
		// TEMPORARY SOLUTION FOR 1.13.2: CHECK IF BEDENTERRESULT WORKS, IF NOT THEN RUN 1.13.1 CODE
		try {
			if (event.getBedEnterResult() == BedEnterResult.OK) {
				Main.bypassers.clear();
				if (Main.plugin.getConfig().getBoolean("features.bypass")) {
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						if (player.hasPermission("harbor.bypass")) {
							Main.bypassers.add(player.getName());
						}
					}
				}
			    
				if (!event.getPlayer().hasPermission("harbor.bypass")) {
					try {
						Main.worlds.put(event.getPlayer().getWorld(), Integer.valueOf((Main.worlds.get(event.getPlayer().getWorld())).intValue() + 1));	
					}
					catch (Exception exception) {
						// Create value if it doesn't exist already
						Main.worlds.put(event.getPlayer().getWorld(), 1);
					}
			      
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
			    
				// Reset value if below zero (plugin glitch)
				if ((Main.worlds.get(event.getPlayer().getWorld())).intValue() < 0) {
					Main.worlds.put(event.getPlayer().getWorld(), Integer.valueOf(0));
				}
			}
		}
		catch (NoSuchMethodError e){
			Main.bypassers.clear();
			if (Main.plugin.getConfig().getBoolean("features.bypass")) {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.hasPermission("harbor.bypass")) {
						Main.bypassers.add(player.getName());
					}
				}
			}
		    
			if (!event.getPlayer().hasPermission("harbor.bypass")) {
				try {
					Main.worlds.put(event.getPlayer().getWorld(), Integer.valueOf((Main.worlds.get(event.getPlayer().getWorld())).intValue() + 1));	
				}
				catch (Exception exception) {
					// Create value if it doesn't exist already
					Main.worlds.put(event.getPlayer().getWorld(), 1);
				}
		      
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
		    
			// Reset value if below zero (plugin glitch)
			if ((Main.worlds.get(event.getPlayer().getWorld())).intValue() < 0) {
				Main.worlds.put(event.getPlayer().getWorld(), Integer.valueOf(0));
			}
		}
	}
}
