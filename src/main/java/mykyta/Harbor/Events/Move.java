package mykyta.Harbor.Events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import mykyta.Harbor.Config;
import mykyta.Harbor.Util;

public class Move implements Listener {
    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Config config = new Config();

        if (Util.afk.contains(p)) {
            Util.afk.remove(p);
            p.setPlayerListName(ChatColor.translateAlternateColorCodes('&', p.getName()));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.unafk")
            .replace("[player]", p.getName())));
        }
        Util.activity.put(event.getPlayer(), System.currentTimeMillis());
    }
}