package mykyta.Harbor.GUI;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHolder implements InventoryHolder {
    private Inventory inv;
    private GUIType type;

    public GUIHolder(GUIType type) {
        this.type = type;
    }

    @Override
    public Inventory getInventory() {
        return inv; // Required by InventoryHolder
    }

    public GUIType getType() {
        return type;
    }
}
