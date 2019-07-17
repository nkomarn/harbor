package xyz.nkomarn.Harbor.event;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import xyz.nkomarn.Harbor.util.Config;

public class SpawnEvent implements Listener {
    private Config c = new Config();

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        Config config = new Config();
        try {if (e.getEntityType().equals(EntityType.PHANTOM)
                && !config.getBoolean("features.phantoms")) e.setCancelled(true);}
        catch (NoSuchFieldError nsf) {
            // Do nothing, Phantom only exists in 1.13+
        }
    }
}
