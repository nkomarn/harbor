package xyz.nkomarn.Harbor.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.gui.SleepingGUI;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Counters;

import java.util.ArrayList;

public class SleepingCommand implements CommandExecutor {
    private Config c = new Config();

    @Override
    public boolean onCommand(CommandSender s, Command x, String l, String[] a) {
        if (s instanceof Player) {
            Player p = (Player) s;
            new SleepingGUI(p, 1);
        }
        else {
            if (a.length < 1)  {
                ArrayList<String> list = new ArrayList<>();
                Bukkit.getServer().getWorlds().forEach(w -> {list.add("&o" + w.getName());});
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        c.getString("messages.miscellaneous.prefix")
                                + "Specify a world name. Possible worlds are " + String.join("&r, ", list) + "&r."));
            }
            else {
                World w = Bukkit.getServer().getWorld(a[0]);
                if (w == null) Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', c.getString("messages.miscellaneous.prefix") + "The specified world doesn't exist."));
                else {
                    ArrayList<String> list = new ArrayList<String>();
                    Counters.sleeping.get(Bukkit.getServer().getWorld(a[0])).forEach(p -> {list.add("&o" + p.getName());});
                    if (list.size() > 0 && list.size() != 1) Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', c.getString("messages.miscellaneous.prefix") + "In &o" + a[0] + "&r, the currently sleeping players are " + String.join("&r, ", list) + "&r."));
                    else if (list.size() == 1) Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', c.getString("messages.miscellaneous.prefix") + "In &o" + a[0] + "&r, " + list.get(0) + "&r is the only player sleeping."));
                    else Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', c.getString("messages.miscellaneous.prefix") + "There aren't any currently sleeping players in &o" + a[0] + "&r."));
                }
            }
        }
        return true;
    }
}
