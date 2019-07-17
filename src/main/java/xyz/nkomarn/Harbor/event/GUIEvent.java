package xyz.nkomarn.Harbor.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import xyz.nkomarn.Harbor.gui.GUIHolder;
import xyz.nkomarn.Harbor.gui.GUIType;
import xyz.nkomarn.Harbor.gui.SleepingGUI;
import xyz.nkomarn.Harbor.util.Counters;

public class GUIEvent implements Listener {
    private Counters n = new Counters();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        n.updateActivity((Player) e.getWhoClicked());
        Inventory i = e.getInventory();
        if (i.getHolder() != null && i.getHolder() instanceof GUIHolder) {
            GUIType t = ((GUIHolder) i.getHolder()).getType();
            final Player p = (Player) e.getWhoClicked();
            e.setCancelled(true);

            if (t.equals(GUIType.SLEEPING)) {
                int page = ((GUIHolder) i.getHolder()).getPage();
                if (e.getSlot() == 39) {
                    if (page <= 1) {e.getWhoClicked().closeInventory();}
                    else new SleepingGUI(p, Math.max(1, --page));
                }
                else if (e.getSlot() == 41) {
                    new SleepingGUI(p, Math.max(1, ++page));
                }
            }
        }
    }
}
