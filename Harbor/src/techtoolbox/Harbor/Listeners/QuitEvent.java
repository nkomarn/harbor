package techtoolbox.Harbor.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import techtoolbox.Harbor.Main;

public class QuitEvent implements Listener{
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Main.bypassers.clear();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.hasPermission("harbor.bypass")) {
				Main.bypassers.add(player.getName());
			}
		}
	}
}
