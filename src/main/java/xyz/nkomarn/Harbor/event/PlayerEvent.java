package xyz.nkomarn.Harbor.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.nms.NMSUtils;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Counters;
import xyz.nkomarn.Harbor.util.Updater;

import java.util.ArrayList;

public class PlayerEvent implements Listener {
    private Config config = new Config();
    private NMSUtils nms = new NMSUtils();
    private Counters counters = new Counters();
    private Updater updater = new Updater();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String json = "[{\"text\":\"[prefix]§7Hey there, Harbor [version] was released! \"},{\"text\":\"§7§oClick §7§ome §7§oto §7§oupdate!\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/harbor update\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§a§l↑ §7Update Harbor.\"}}]";
        if (p.isOp() && updater.check() && config.getBoolean("features.notifier")) nms.sendJSONMessage(p, json.replace("[version]", updater.getLatest()).replace("[prefix]", config.getString("messages.miscellaneous.prefix")).replace("&", "§"));
        Counters.activity.put(p, System.currentTimeMillis());
        if (p.isOp() && Harbor.prerelease) p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                config.getString("messages.miscellaneous.prefix") + "&cThis Harbor version is a prerelease. Not everything is guaranteed to work correctly, but the plugin should at least be stable. "
                + "If you encounter an issue, please create an issue on GitHub: &c&ohttps://github.com/nkomarn/Harbor/issues&c."));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBedEnter(PlayerBedEnterEvent e) {
        if (config.getBoolean("features.block")) {
            if (config.getString("messages.chat.blocked").length() > 0) e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.chat.blocked")));
            nms.sendActionbar(e.getPlayer(), config.getString("messages.actionbar.blocked"));
            e.setCancelled(true);
            return;
        }

        boolean success = false;
        try {if (e.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) success = true;}
        catch (NoSuchMethodError nme) {success = true;}

        if (success) {
            Player p = e.getPlayer();
            World w = p.getWorld();
            ArrayList<Player> ex = counters.getExcluded(w);

            if (!ex.contains(p)) {
                counters.add(w, p);
                if (config.getBoolean("messages.chat.chat") && (config.getString("messages.chat.sleeping").length() != 0)) {
                    Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            config.getString("messages.chat.sleeping")
                                    .replace("[sleeping]", String.valueOf(counters.getSleeping(w))))
                            .replace("[online]", String.valueOf(counters.getOnline(w)))
                            .replace("[player]", p.getName())
                            .replace("[needed]", String.valueOf(counters.getNeeded(w))));
                }
                if (config.getBoolean("messages.title.title")) {
                    nms.sendTitle(p, config.getString("messages.title.sleeping.top"),
                            config.getString("messages.title.sleeping.bottom"));
                }
                counters.skip(w);
            }
            else if (config.getString("messages.chat.bypass").length() != 0)
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.bypass")));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBedLeave(PlayerBedLeaveEvent e) {
        Player p = e.getPlayer();
        World w = p.getWorld();

        ArrayList<Player> ex = counters.getExcluded(w);
        if (!ex.contains(p)) counters.remove(w, p);
        if (config.getBoolean("messages.chat.chat") && (config.getString("messages.chat.left").length() != 0)
                && !(w.getTime() > 0 && w.getTime() < 12300) && !ex.contains(p)) {
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.left")
                    .replace("[sleeping]", String.valueOf(counters.getSleeping(w))))
                    .replace("[online]", String.valueOf(counters.getOnline(w)))
                    .replace("[player]", p.getName())
                    .replace("[needed]", String.valueOf(counters.getNeeded(w))));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        counters.updateActivity(e.getPlayer());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        counters.updateActivity(e.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        counters.updateActivity(e.getPlayer());
    }
}
