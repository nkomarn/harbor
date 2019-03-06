package mykyta.Harbor.Events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;

import mykyta.Harbor.Config;
import mykyta.Harbor.Util;

public class BedEnter implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Util util = new Util();
        Config config = new Config();

        //Prevent bed entry if "block" is enabled in the config
		if (config.getBoolean("features.block")) {
			if (config.getString("messages.chat.blocked").length() > 0) event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.blocked")));	
			util.sendActionbar(event.getPlayer(), config.getString("messages.actionbar.blocked"));
            event.setCancelled(true);	
        }
        
        if (event.getBedEnterResult() == BedEnterResult.OK) {
            World world = event.getPlayer().getWorld();

            // Increment the sleeping count
            if (!config.getBoolean("features.bypass") || !event.getPlayer().hasPermission("harbor.bypass")) {
                util.increment(world);
            }
            else if (config.getString("messages.chat.bypass").length() != 0) event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.bypass")));
        
            // Send a chat message 
			if (config.getBoolean("messages.chat.chat") && (config.getString("messages.chat.sleeping").length() != 0)) {
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.sleeping")
                .replace("[sleeping]", String.valueOf(util.fetch(world))))
                .replace("[online]", String.valueOf(world.getPlayers().size()))
                .replace("[player]", event.getPlayer().getName())
                .replace("[needed]", String.valueOf(Math.max(0, Math.round(world.getPlayers().size() * Float.parseFloat(config.getString("values.percent")) - util.fetch(world))))));
            }
        }
    }
}