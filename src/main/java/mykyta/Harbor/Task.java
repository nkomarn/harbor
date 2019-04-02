package mykyta.Harbor;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Task implements Runnable {

    Util util = new Util();
    Config config = new Config();

    @Override
    public void run() {
        Bukkit.getServer().getWorlds().forEach(w -> {
            // Display a title if it's time to sleep
            if (w.getTime() >= 12516 && w.getTime() <= 12547) w.getPlayers().forEach(p -> {
                util.sendTitle(p, config.getString("messages.title.evening.top"), config.getString("messages.title.evening.bottom"));
            });

            // Fix time if players leave bed naturally
            /*if (w.getTime() >= 0 && w.getTime() <= 100) {
                ArrayList<Player> list = new ArrayList<Player>();
                Util.sleeping.put(w, list);
            }*/

            // Send actionbar if someone's sleeping
            if (util.getSleeping(w) > 0 && util.getSleeping(w) < w.getPlayers().size()) {w.getPlayers().forEach(p -> {util.sendActionbar(p, config.getString("messages.actionbar.sleeping"), w);});} 
            else if (util.getSleeping(w) == w.getPlayers().size()) {w.getPlayers().forEach(p -> {util.sendActionbar(p, config.getString("messages.actionbar.everyone"), w);});}
        });

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
}