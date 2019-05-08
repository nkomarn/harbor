package mykyta.Harbor.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import mykyta.Harbor.GUI.GUIType;
import mykyta.Harbor.GUI.GUIHolder;

public class GUIEvent implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof Holder) {
            Player p = ((Player) event.getWhoClicked());
            GUIType t = ((GUIHolder) event.getInventory().getHolder()).getType();
            if (t.equals(GUIType.SLEEPING)) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof Holder) {
            Player p = ((Player) event.getWhoClicked());
            GUIType t = ((GUIHolder) event.getInventory().getHolder()).getType();
            if (t.equals(GUIType.SLEEPING)) event.setCancelled(true);
        }
    }
}