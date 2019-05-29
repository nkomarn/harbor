package mykyta.Harbor.Events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import mykyta.Harbor.Util;

public class PlayerLeave implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Util util = new Util();
        Player p = event.getPlayer();
        World w = p.getWorld();

        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    if (w.getPlayers().size() > 0 && Math.max(0, util.getNeeded(w) - util.getExcluded(w).size()) == 0 && !Util.skipping) util.skip(w);
                    Util.activity.remove(p);
                }
            }, 
            1000 
        );
    }
}