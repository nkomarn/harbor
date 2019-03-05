package mykyta.Harbor.Events;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;

import mykyta.Harbor.Harbor;
import mykyta.Harbor.Util;

public class BedEnter implements Listener {
    
    Harbor harbor;
    public BedEnter(Harbor instance) {
        harbor = instance;
    }

    @EventHandler(priority = EventPriority.HIGH)
	public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
        Util util = new Util(harbor);

        /**
         * Prevent bed entry if "blockSleep" is enabled in the config
         */
		if (harbor.getConfig().getBoolean("features.block")) {
			if (harbor.getConfig().getString("messages.chat.blocked").length() > 0) event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', harbor.getConfig().getString("messages.chat.blocked")));	
			util.sendActionbar(event.getPlayer(), harbor.getConfig().getString("messages.actionbar.blocked"));
            event.setCancelled(true);	
        }
        
        /**
         * Increment world's sleeping count if player isn't excluded
         */
        if (event.getBedEnterResult() == BedEnterResult.OK) {
            if (!harbor.getConfig().getBoolean("features.bypass") || !event.getPlayer().hasPermission("harbor.bypass")) {
                World world = event.getPlayer().getWorld();
                Util.sleeping.put(world, Util.sleeping.get(world) + 1);
            }
            else if (harbor.getConfig().getString("messages.chat.bypass").length() != 0) event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', harbor.getConfig().getString("messages.chat.bypass")));
        }
    }
}