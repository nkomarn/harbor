package mykyta.Harbor.Events;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
            World w = event.getPlayer().getWorld();
            ArrayList<Player> included = util.getIncluded(w);
            int excluded = w.getPlayers().size() - included.size();

            // Increment the sleeping count TODO bypass stuff
            if (included.contains(event.getPlayer())) {
                util.add(w, event.getPlayer());

                // Send a chat message when a player is sleeping
                if (config.getBoolean("messages.chat.chat") && (config.getString("messages.chat.sleeping").length() != 0)) {
                    Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.sleeping")
                    .replace("[sleeping]", String.valueOf(util.getSleeping(w))))
                    .replace("[online]", String.valueOf(included.size()))
                    .replace("[player]", event.getPlayer().getName())
                    .replace("[needed]", String.valueOf(util.getNeeded(w) - excluded)));
                }
            }
            else if (config.getString("messages.chat.bypass").length() != 0) event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.bypass")));

            // Skip night if possible
            util.skip(w, excluded, util.getNeeded(w));
        }
    }
}