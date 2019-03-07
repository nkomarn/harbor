package mykyta.Harbor.Events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import mykyta.Harbor.Config;
import mykyta.Harbor.Updater;
import mykyta.Harbor.Util;

public class PlayerJoin implements Listener {
    @EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
        Updater updater = new Updater();
        Config config = new Config();
        Util util = new Util();

        String json = "[{\"text\":\"[prefix]§7Hey there, Harbor [version] was released! \"},{\"text\":\"§7§oClick §7§ome §7§oto §7§oupdate!\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/harbor update\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§a§l↑ §7Update Harbor.\"}}]";
        if (event.getPlayer().isOp() && updater.check()) util.sendJSONMessage(event.getPlayer(), json.replace("[version]", updater.getLatest()).replace("[prefix]", config.getString("messages.miscellaneous.prefix")).replace("&", "§"));
    }
}