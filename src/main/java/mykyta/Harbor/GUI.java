package mykyta.Harbor;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A custom Inventory wrapper for Harbor GUIs
 * @see https://www.spigotmc.org/threads/custom-inventory-types.150414/
 */
public class GUI {

    //TODO ADD JAVADOC COMMENTS ALL OVER THIS FILE

    public enum GUIType {
        SLEEPING, BLACKLIST
    }

    private final Inventory inventory;
    private final GUIType type;

    public GUI(Inventory inventory, GUIType type) {
        this.inventory = inventory;
        this.type = type;
    }

    public GUI(int size, String name, GUIType type) {
        this.inventory = Bukkit.createInventory(null, size, name);
        this.type = type;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public GUIType getType() {
        return this.type;
    }

    public void setItem(int slot, ItemStack itemstack) {
        this.inventory.setItem(slot, itemstack);
    }
}