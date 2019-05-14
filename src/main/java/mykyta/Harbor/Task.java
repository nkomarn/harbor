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
            if (w.getTime() >= 12516 && w.getTime() <= 12537 && config.getBoolean("features.title")) w.getPlayers().forEach(p -> {
                util.sendTitle(p, config.getString("messages.title.evening.top"), config.getString("messages.title.evening.bottom"));
            });
            if (util.getSleeping(w) > 0 && util.getNeeded(w) == 0) {
                util.skip(w);  
            } 
            if (util.getSleeping(w) > 0 && util.getSleeping(w) < w.getPlayers().size()) {w.getPlayers().forEach(p -> {util.sendActionbar(p, config.getString("messages.actionbar.sleeping"), w);});} 
            else if (util.getSleeping(w) == util.getNeeded(w)) {w.getPlayers().forEach(p -> {util.sendActionbar(p, config.getString("messages.actionbar.everyone"), w);});}
            
            w.getPlayers().forEach(p -> {
                if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - Util.activity.get(p)) >= config.getInteger("values.timeout")) {
                    if (Util.sleeping.get(w).contains(p)) {
                        Util.activity.put(p, System.currentTimeMillis());
                        return;
                    }
                    if (!Util.afk.contains(p)) {
                        Util.afk.add(p);
                        p.setPlayerListName(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.afkprefix") + p.getName()));
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.afk")
                        .replace("[player]", p.getName())));
                    }
                }
            });
        });
    }
}