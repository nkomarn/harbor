package mykyta.Harbor.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import mykyta.Harbor.Config;

public class GUIClick implements Listener {
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onGUIClick(InventoryClickEvent event) {
        Config config = new Config();
        String title = event.getInventory().getName();
        
        if (title.equals(config.getString("gui.sleeping"))) {
            event.setCancelled(true);
        }
    }
}