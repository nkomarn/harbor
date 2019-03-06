package mykyta.Harbor.Events;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
            World world = event.getPlayer().getWorld();

            // Create list of players included in sleep count
            ArrayList<Player> players = new ArrayList<Player>();
            world.getPlayers().stream().filter(p -> util.isSurvival(event.getPlayer())).forEach(p -> {
                if (true) {
                    players.add(p);
                }
            });

            System.out.println(players);
            System.out.println("Included players: " + players.size());

            // Increment the sleeping count TODO bypass stuff
            if (players.contains(event.getPlayer())) {
                util.increment(world);

                // Send a chat message when a player is sleeping
                if (config.getBoolean("messages.chat.chat") && (config.getString("messages.chat.sleeping").length() != 0)) {
                    Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.sleeping")
                    .replace("[sleeping]", String.valueOf(util.fetch(world))))
                    .replace("[online]", String.valueOf(world.getPlayers().size()))
                    .replace("[player]", event.getPlayer().getName())
                    .replace("[needed]", String.valueOf(Math.max(0, (int) Math.ceil(world.getPlayers().size() * (config.getDouble("values.percent") / 100) - util.fetch(world))))));
                }
            }
            else if (config.getString("messages.chat.bypass").length() != 0) event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.bypass")));
        
            // Skip night if threshold is reached
            if (config.getBoolean("features.skip") && (util.fetch(world) >= (int) Math.ceil(world.getPlayers().size() * (config.getDouble("values.percent") / 100) - util.fetch(world)))) {
                Bukkit.getServer().getWorld(event.getPlayer().getWorld().getName()).setTime(1000L);
                
                // Clear weather when it turns day
                if (config.getBoolean("features.clearWeather")) {
                    event.getPlayer().getWorld().setStorm(false);
                    event.getPlayer().getWorld().setThundering(false);
                }
                    
                // Send a chat message when night is skipped
                if (config.getBoolean("messages.chat.chat") && (config.getString("messages.chat.skipped").length() != 0)) {
                    Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.skipped")));
                }
            }
        }
    }
}