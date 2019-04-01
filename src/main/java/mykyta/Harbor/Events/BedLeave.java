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
        World world = event.getPlayer().getWorld();

        ArrayList<Player> included = util.getIncluded(world);
        int excluded = world.getPlayers().size() - included.size();
        
        // Decrement the sleeping count if player isn't excluded
        if (included.contains(event.getPlayer())) {
            util.remove(world, event.getPlayer());
        }
        
        // Send a chat message when player gets out of bed (if it's not day)
		if (config.getBoolean("messages.chat.chat") && (config.getString("messages.chat.sleeping").length() != 0) && Bukkit.getServer().getWorld(world.getName()).getTime() < 1000L) {
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.sleeping")
            .replace("[sleeping]", String.valueOf(util.getSleeping(world))))
            .replace("[online]", String.valueOf(included.size()))
            .replace("[player]", event.getPlayer().getName())
            .replace("[needed]", String.valueOf(util.getNeeded(world) - excluded)));
        }
    }
}