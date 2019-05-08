package mykyta.Harbor.Events;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import mykyta.Harbor.Config;

public class Spawn implements Listener {
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        Config config = new Config();
        try {
            if (event.getEntityType().equals(EntityType.PHANTOM) && !config.getBoolean("features.phantoms")) event.setCancelled(true);  
        }
        catch (NoSuchFieldError e) {
            // Do nothing, Phantom only exists in 1.13+
        }
    }
}