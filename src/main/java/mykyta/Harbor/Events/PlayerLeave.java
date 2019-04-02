package mykyta.Harbor.Events;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import mykyta.Harbor.Util;

public class PlayerLeave implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Util util = new Util();
        World w = event.getPlayer().getWorld();
        int excluded = (w.getPlayers().size() - 1) - (util.getIncluded(w).size() - 1);
        util.skip(event.getPlayer().getWorld(), excluded, util.getNeededDecremented(w));
        Util.activity.remove(event.getPlayer());
    }
}