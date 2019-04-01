package mykyta.Harbor;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Task implements Runnable {

    Util util = new Util();
    Config config = new Config();

    @Override
    public void run() {
        // Display a title if it's time to sleep
        for (World world : Bukkit.getWorlds()) {
            if (world.getTime() >= 12516l && world.getTime() <= 12547l) {
                System.out.println("sleepy time");

                Bukkit.getWorld(world.getUID()).getPlayers().stream().forEach(p -> {
                    util.sendTitle(p, config.getString("messages.title.evening.top"), config.getString("messages.title.evening.bottom"));
                });
            }
        }

        /*
        // TODO if player hasnt moved for x minutes then put in afk
        Bukkit.getServer().getWorlds().forEach(w -> {
            w.getPlayers().forEach(p -> {
                if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - Util.activity.get(p)) > config.getInteger("values.timeout")) {
                    ///TODO custom afk prefix
                    p.setPlayerListName(ChatColor.GRAY + "[AFK] - " + ChatColor.RESET + p.getDisplayName());
                    System.out.println(p.getName() + " is AFK.");
                }
            });
        });*/ 
    }

   /* @Override
    public void run() {
        try {
            for (int i = 0; Main.worlds.size() > i; i++) {
                if (((Main.worlds.get(Bukkit.getServer().getWorlds().get(i))).intValue() > 0) && ((Main.worlds.get(Bukkit.getServer().getWorlds().get(i))).intValue() < (Bukkit.getServer().getWorlds().get(i)).getPlayers().size() - Main.bypassers.size())) {
                    Main.sendActionbar("playersInBed", Bukkit.getServer().getWorlds().get(i));
                }
                else if (((Main.worlds.get(Bukkit.getServer().getWorlds().get(i))).intValue() == (Bukkit.getServer().getWorlds().get(i)).getPlayers().size() - Main.bypassers.size()) && ((Bukkit.getServer().getWorlds().get(i)).getPlayers().size() - Main.bypassers.size() > 1)) {
                    Main.sendActionbar("everyoneSleeping", Bukkit.getServer().getWorlds().get(i));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}