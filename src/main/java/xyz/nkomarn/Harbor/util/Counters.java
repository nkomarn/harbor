package xyz.nkomarn.Harbor.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.nms.NMSUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Counters {
    public static HashMap<World, ArrayList<Player>> sleeping = new HashMap<>();
    public static HashMap<Player, Long> activity = new HashMap<>();
    public static ArrayList<Player> afk = new ArrayList<>();

    private Config c = new Config();
    private NMSUtils nms = new NMSUtils();

    public void add(World w, Player p) {
        ArrayList<Player> a;
        try {
            a = sleeping.get(w);
        } catch (NullPointerException e) {
            a = new ArrayList<>();
        }
        a.add(p);
        sleeping.put(w, a);
    }

    public void remove(World w, Player p) {
        ArrayList<Player> a = sleeping.get(w);
        a.remove(p);
    }

    public int getSleeping(World w) {
        try {return Math.max(0, sleeping.get(w).size());}
        catch (NullPointerException e) {
            if (Harbor.debug) e.printStackTrace();
            return 0;
        }
    }

    public int getNeeded(World w) {
        try {return Math.max(0, (int) Math.ceil((w.getPlayers().size() - getExcluded(w).size()) * (c.getDouble("values.percent") / 100) - getSleeping(w)));}
        catch (NullPointerException e) {
            if (Harbor.debug) e.printStackTrace();
            return 0;
        }
    }

    public int getOnline(World w) {
        try {return Math.max(0, w.getPlayers().size() - getExcluded(w).size());}
        catch (NullPointerException e) {return 0;}
    }

    public ArrayList<Player> getExcluded(World w) {
        ArrayList<Player> a = new ArrayList<>();
        w.getPlayers().forEach(p -> {
            if (isExcluded(p)) a.add(p);
        });
        return a;
    }

    private boolean isExcluded(Player p) {
        boolean s = false;
        if (c.getBoolean("features.ignore")) if (p.getGameMode() == GameMode.SURVIVAL) s = false; else s = true;
        if (c.getBoolean("features.bypass")) if (p.hasPermission("harbor.bypass")) s = true; else s = false;
        if (afk.contains(p)) s = true;
        return s;
    }

    public void delayedSkip(World w) {
        Bukkit.getScheduler().runTaskLater(Harbor.instance,
                () -> skip(w),
                c.getInteger("values.delay") * 20
        );
    }

    public void skip(World w) {
        if (c.getBoolean("features.skip") && Math.max(0, this.getNeeded(w)) == 0) {
            w.setTime(1000L);

            // Synchronously set weather to clear
            if (c.getBoolean("features.weather")) {
                Bukkit.getScheduler().runTask(Harbor.instance, () -> {
                    w.setStorm(false);
                    w.setThundering(false);
                });
            }

            // Display messages
            if (c.getBoolean("messages.chat.chat") && (c.getString("messages.chat.skipped").length() != 0)) {
                List<String> m = c.getList("messages.chat.skipped");
                Random r = new Random();
                int n = r.nextInt(m.size());
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', m.get(n)));
            }
            if (c.getBoolean("messages.title.title")) {
                w.getPlayers().forEach(p -> {
                    nms.sendTitle(p, c.getString("messages.title.morning.top"), c.getString("messages.title.morning.bottom"));
                });
            }
        }
    }

    public void updateActivity(Player p) {
        if (afk.contains(p)) {
            afk.remove(p);
            p.setPlayerListName(ChatColor.translateAlternateColorCodes('&', p.getDisplayName()));
            if (c.getString("messages.chat.unafk").length() > 0)
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                        c.getString("messages.chat.unafk").replace("[player]", p.getName())));
        }
        activity.put(p, System.currentTimeMillis());
    }
}
