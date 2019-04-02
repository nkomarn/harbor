package mykyta.Harbor.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import mykyta.Harbor.Config;
import mykyta.Harbor.EncodingUtils;
import mykyta.Harbor.Util;

public class Sleeping implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            World world = player.getWorld();
            Config config = new Config();
            ArrayList<Player> sleeping = Util.sleeping.get(world);
            int slots = (((sleeping.size()) % 9)) * 9; //FIXME bad brok bad
            System.out.println(slots);
            Inventory gui = Bukkit.createInventory(player, slots, config.getString("gui.sleeping"));
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
            Config config = new Config();
            if (args.length < 1)  {
                ArrayList<String> list = new ArrayList<String>();
                Bukkit.getServer().getWorlds().forEach(w -> {list.add("&o" + w.getName());});
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + "Specify a world name. Possible worlds are " + String.join("&r, ", list) + "&r."));
            }
            else {
                World w = Bukkit.getServer().getWorld(args[0]);
                if (w == null) Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + "The specified world doesn't exist."));
                else {
                    ArrayList<String> list = new ArrayList<String>();
                    Util.sleeping.get(Bukkit.getServer().getWorld(args[0])).forEach(p -> {list.add("&o" + p.getName());});
                    if (list.size() > 0 && list.size() != 1) Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + "In &o" + args[0] + "&r, the currently sleeping players are " + String.join("&r, ", list) + "&r."));
                    else if (list.size() == 1) Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + "In &o" + args[0] + "&r, " + list.get(0) + "&r is the only player sleeping."));
                    else Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + "There aren't any currently sleeping players in &o" + args[0] + "&r."));
                }
            }
            /*
            StringBuilder sb = new StringBuilder();
            ArrayList<Player> sleeping = Util.sleeping.get(w);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")));
            */
        }
        return true;
    }
}