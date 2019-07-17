package xyz.nkomarn.Harbor.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.nkomarn.Harbor.gui.GUIHolder;
import xyz.nkomarn.Harbor.gui.GUIType;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Counters;

import java.util.ArrayList;

public class SleepingGUI {

    private Inventory i;
    Config c = new Config();

    public SleepingGUI(Player p, int page) {
        ArrayList<Player> s = Counters.sleeping.get(p.getWorld());
        i = Bukkit.createInventory(new GUIHolder(GUIType.SLEEPING, page),
                45, c.getString("gui.sleeping").replace("[page]", String.valueOf(page)));

        ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        int[] slots = {36, 37, 38, 40, 42, 43, 44};
        for (int x = 0; x < slots.length; x++) {i.setItem(slots[x], glass);}

        ItemStack bb = new ItemStack(Material.OAK_BUTTON, 1);
        ItemMeta bbm = bb.getItemMeta();
        bbm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lPrevious"));
        bb.setItemMeta(bbm);
        i.setItem(39, bb);

        ItemStack nb = new ItemStack(Material.OAK_BUTTON, 1);
        ItemMeta nbm = nb.getItemMeta();
        nbm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lNext"));
        nb.setItemMeta(nbm);
        i.setItem(41, nb);

        int f = (((page - 1) * 35) + (page + 1) - 1) - 1;
        int l = Math.min(s.size(), (f + 36));

        int t = 0;
        for (int c = f; c < l; c++) {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta sm = (SkullMeta) item.getItemMeta();
            sm.setDisplayName(ChatColor.GRAY + s.get(c).getName());
            sm.setOwningPlayer(p);
            item.setItemMeta(sm);
            i.setItem(s.indexOf(s.get(c)), item);
            t++;
        }
        p.openInventory(i);
    }
}
