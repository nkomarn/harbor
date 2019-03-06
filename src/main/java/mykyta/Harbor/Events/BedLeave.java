package mykyta.Harbor.Events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import mykyta.Harbor.Config;
import mykyta.Harbor.Util;

public class BedLeave implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
	public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        Util util = new Util();
        Config config = new Config();
        World world = event.getPlayer().getWorld();
        
        // Decrement the sleeping count
        if (!config.getBoolean("features.bypass") || !event.getPlayer().hasPermission("harbor.bypass")) {
            util.decrement(world);
        }
        else if (config.getString("messages.chat.bypass").length() != 0) event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.bypass")));
        
        // Send a chat message 
		if (config.getBoolean("messages.chat.chat") && (config.getString("messages.chat.sleeping").length() != 0)) {
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.left")
            .replace("[sleeping]", String.valueOf(util.fetch(world))))
            .replace("[online]", String.valueOf(world.getPlayers().size()))
            .replace("[player]", event.getPlayer().getName())
            .replace("[needed]", String.valueOf(Math.max(0, Math.round(world.getPlayers().size() * Float.parseFloat(config.getString("values.percent")) - util.fetch(world))))));
        }
    }
}