package mykyta.Harbor.Events;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
        Player p = event.getPlayer();
        World w = p.getWorld();
        
        ArrayList<Player> excluded = util.getExcluded(w);
        
        // Decrement the sleeping count if player isn't excluded
        if (!excluded.contains(p)) {
            util.remove(w, p);
        }
        
        // Send a chat message when player gets out of bed (if it's not day)
		if (config.getBoolean("messages.chat.chat") && (config.getString("messages.chat.left").length() != 0) && !(w.getTime() > 0 && w.getTime() < 12300) && !excluded.contains(p)) {
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.left")
            .replace("[sleeping]", String.valueOf(util.getSleeping(w))))
            .replace("[online]", String.valueOf(util.getOnline(w) - excluded.size()))
            .replace("[player]", event.getPlayer().getName())
            .replace("[needed]", String.valueOf(util.getNeeded(w) - excluded.size())));
        }
    }
}