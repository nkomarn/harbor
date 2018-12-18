package techtoolbox.Harbor.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import techtoolbox.Harbor.Main;

public class JoinEvent implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Main.bypassers.clear();
		if (Main.plugin.getConfig().getBoolean("features.bypass")) {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.hasPermission("harbor.bypass")) {
					Main.bypassers.add(player.getName());
				}
			}
		}
	}
}
