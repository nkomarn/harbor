package xyz.nkomarn.Harbor.task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.nms.NMSUtils;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Counters;

import java.util.concurrent.TimeUnit;

public class Timer implements Runnable {
    Config c = new Config();
    Counters n = new Counters();
    NMSUtils nms = new NMSUtils();

    @Override
    public void run() {
        try {
            Bukkit.getServer().getWorlds().forEach(w -> {
                if (w.getTime() >= 12516 && w.getTime() <= 12535 && c.getBoolean("messages.title.title"))
                    w.getPlayers().forEach(p -> nms.sendTitle(p, c.getString("messages.title.evening.top"), c.getString("messages.title.evening.bottom")));
                if (n.getSleeping(w) > 0 && n.getNeeded(w) == 0) n.skip(w);

                if (n.getSleeping(w) > 0 && n.getSleeping(w) < n.getNeeded(w))
                    w.getPlayers().forEach(p -> nms.sendActionbar(p, c.getString("messages.actionbar.sleeping"), w));
                else if (n.getSleeping(w) == n.getNeeded(w) && n.getSleeping(w) > 0)
                    w.getPlayers().forEach(p -> nms.sendActionbar(p, c.getString("messages.actionbar.everyone"), w));

                if (c.getBoolean("features.afk")) w.getPlayers().forEach(p -> {
                    if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - Counters.activity.get(p)) >= c.getInteger("values.timeout")) {
                        if (Counters.sleeping.get(w).contains(p)) {
                            Counters.activity.put(p, System.currentTimeMillis());
                            return;
                        }
                        if (!Counters.afk.contains(p)) {
                            Counters.afk.add(p);
                            p.setPlayerListName(ChatColor.translateAlternateColorCodes('&', c.getString("messages.miscellaneous.afkprefix")
                                    + p.getDisplayName()));
                            if (c.getString("messages.chat.afk").length() > 0)
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', c.getString("messages.chat.afk")
                                        .replace("[player]", p.getName())));
                        }
                    }
                });
            });
        } catch (Exception e) {
            if (Harbor.debug) e.printStackTrace();
        }
    }
}
