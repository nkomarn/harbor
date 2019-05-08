package mykyta.Harbor;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class Holder implements InventoryHolder {
    Inventory inv;
    GUIType type;

    public Holder(GUIType type) {
        this.type = type;
    }

    @Override
    public Inventory getInventory() {
        return inv; // Required by InvetoryHolder
    }

    public GUIType getType() {
        return type;
    }
}