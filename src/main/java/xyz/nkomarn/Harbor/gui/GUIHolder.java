package xyz.nkomarn.Harbor.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHolder implements InventoryHolder {
    private Inventory inv;
    private GUIType type;
    private int page;

    public GUIHolder(GUIType type, int page) {
        this.type = type;
        this.page = page;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public GUIType getType() {
        return type;
    }

    public int getPage() {
        return this.page;
    }
}
