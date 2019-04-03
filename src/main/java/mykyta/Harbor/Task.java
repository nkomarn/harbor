package mykyta.Harbor;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Task implements Runnable {

    Util util = new Util();
    Config config = new Config();

    @Override
    public void run() {
        Bukkit.getServer().getWorlds().forEach(w -> {
            if (w.getTime() >= 12516 && w.getTime() <= 12547) w.getPlayers().forEach(p -> {
                util.sendTitle(p, config.getString("messages.title.evening.top"), config.getString("messages.title.evening.bottom"));
            });
            if (util.getSleeping(w) > 0 && Math.max(0, util.getNeeded(w) - util.getExcluded(w).size()) == 0) {
                util.skip(w);  
            } 
            if (util.getSleeping(w) > 0 && util.getSleeping(w) < w.getPlayers().size()) {w.getPlayers().forEach(p -> {util.sendActionbar(p, config.getString("messages.actionbar.sleeping"), w);});} 
            else if (util.getSleeping(w) == w.getPlayers().size()) {w.getPlayers().forEach(p -> {util.sendActionbar(p, config.getString("messages.actionbar.everyone"), w);});}
            
            /*w.getPlayers().forEach(p -> {

            });*/
            if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - Util.activity.get(p)) > config.getInteger("values.timeout")) {
                ///TODO custom afk prefix
                //p.setPlayerListName(ChatColor.GRAY + "[AFK] - " + ChatColor.RESET + p.getDisplayName());
                System.out.println(p.getName() + " is AFK.");
            }
        });
    }
}