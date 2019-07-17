package xyz.nkomarn.Harbor.nms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.nms.NMS;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Counters;

public class NMSUtils {

    static Counters n = new Counters();
    static Config c = new Config();
    static NMS nms;

    public void setNMS() {
        String v = "";
        try {v = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];}
        catch (ArrayIndexOutOfBoundsException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(c.getString("messages.miscellaneous.prefix") + "Could not get server version. The plugin may not function correctly as a result.");
            if (Harbor.debug) e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(Harbor.instance);
            Harbor.enabled = false;
        }
        if (Harbor.debug) Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', c.getString("messages.miscellaneous.prefix") + c.getString("messages.miscellaneous.running").replace("[version]", v)));
        if (v.equals("v1_8_R1")) {nms = new NMS_1_8_R1();}
        else if (v.equals("v1_8_R2")) {nms = new NMS_1_8_R2();}
        else if (v.equals("v1_8_R3")) {nms = new NMS_1_8_R3();}
        else if (v.equals("v1_9_R1")) {nms = new NMS_1_9_R1();}
        else if (v.equals("v1_9_R2")) {nms = new NMS_1_9_R2();}
        else if (v.equals("v1_10_R1")) {nms = new NMS_1_10_R1();}
        else if (v.equals("v1_11_R1")) {nms = new NMS_1_11_R1();}
        else if (v.equals("v1_12_R1")) {nms = new NMS_1_12_R1();}
        else if (v.equals("v1_13_R1")) {nms = new NMS_1_13_R1();}
        else if (v.equals("v1_13_R2")) {nms = new NMS_1_13_R2();}
        else if (v.equals("v1_14_R1")) {nms = new NMS_1_14_R1();}
        else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', c.getString("messages.miscellaneous.prefix") + "This version of Harbor is incompatible with your server version. As such, Harbor will be disabled."));
            Bukkit.getPluginManager().disablePlugin(Harbor.instance);
            Harbor.enabled = false;
        }
    }

    public void sendActionbar(Player player, String message) {
        if (c.getBoolean("messages.actionbar.actionbar")) nms.sendActionbar(player, message);
    }
    public void sendActionbar(Player p, String message, World w) {
        if (c.getBoolean("messages.actionbar.actionbar")) nms.sendActionbar(p, message
                .replace("[sleeping]", String.valueOf(n.getSleeping(w)))
                .replace("[online]", String.valueOf(n.getOnline(w)))
                .replace("[needed]", String.valueOf(n.getNeeded(w))));
    }
    public void sendJSONMessage(Player p, String j) {
        nms.sendJSONMessage(p, j);
    }
    public void sendTitle(Player p, String t, String b) {
        nms.sendTitle(p, t, b);
    }
}
