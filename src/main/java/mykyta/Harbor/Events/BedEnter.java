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
	public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
        Util util = new Util();
        Config config = new Config();

        /**
         * Prevent bed entry if "block" is enabled in the config
         */
		if (config.getBoolean("features.block")) {
			if (config.getString("messages.chat.blocked").length() > 0) event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.blocked")));	
			util.sendActionbar(event.getPlayer(), config.getString("messages.actionbar.blocked"));
            event.setCancelled(true);	
        }
        
        /**
         * Increment world's sleeping count if player isn't excluded
         */
        if (event.getBedEnterResult() == BedEnterResult.OK) {
            // Add one to the sleeping list
            if (!config.getBoolean("features.bypass") || !event.getPlayer().hasPermission("harbor.bypass")) {
                util.increment(event.getPlayer().getWorld());
            }
            else if (config.getString("messages.chat.bypass").length() != 0) event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.bypass")));
        
            // Send a chat message 
			if (config.getBoolean("messages.chat.chat") && (config.getString("messages.chat.sleeping").length() != 0)) {
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.sleeping")
                .replace("[sleeping]", String.valueOf(Util.sleeping.get(event.getPlayer().getWorld())))
                .replace("[online]", String.valueOf(event.getPlayer().getWorld().getPlayers().size()))
                .replace("[player]", event.getPlayer().getName())
                .replace("[needed]", String.valueOf(Math.max(0, Math.round(event.getPlayer().getWorld().getPlayers().size() * Float.parseFloat(Main.plugin.getConfig().getString("values.percent")) - Main.bypassers.size() - (Main.worlds.get(event.getPlayer().getWorld())).intValue()))))));
            }
        }
    }
}