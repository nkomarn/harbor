package mykyta.Harbor.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import mykyta.Harbor.Config;
import mykyta.Harbor.EncodingUtils;
import mykyta.Harbor.GUI;
import mykyta.Harbor.Util;

public class Sleeping implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String args, String[] label) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            World world = player.getWorld();
            Config config = new Config();
            String title = config.getString("gui.sleeping") + " " + EncodingUtils.encodeString("{'GUIType': 'SLEEPING'}");
            Inventory gui = Bukkit.createInventory(player, 9, title);

            ArrayList<Player> sleeping = Util.sleeping.get(world);
            if (sleeping.size() > 0) sleeping.forEach(p -> {
                ItemStack item = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) 3); //FIXME deprecated
                SkullMeta skull = (SkullMeta) item.getItemMeta();
                skull.setDisplayName(p.getName());
                /*ArrayList<String> lore = new ArrayList<String>();
                lore.add("Custom head");
                skull.setLore(lore);
                */
                skull.setOwner(p.getName());
                item.setItemMeta(skull);
                gui.setItem(sleeping.indexOf(p), item);
            });

            player.openInventory(gui);
        }
        else {
            //TODO console stuff
        }
       


        return true;
    }
}