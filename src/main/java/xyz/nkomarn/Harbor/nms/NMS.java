package xyz.nkomarn.Harbor.nms;

import org.bukkit.entity.Player;

public interface NMS {
    void sendActionbar(Player p, String m);
    void sendJSONMessage(Player p, String j);
    void sendTitle(Player p, String t, String n);
}
