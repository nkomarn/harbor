package xyz.nkomarn.Harbor.util;

import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.Harbor;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Afk {
    private static HashMap<Player, Long> activity = new HashMap<>();

    public static boolean isAfk(Player player) {
        if (!Config.getBoolean("features.afk")) return false;

        if (Harbor.essentials != null) {
            return Harbor.essentials.getUser(player).isAfk();
        }

        if (!activity.containsKey(player)) return false;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - activity.get(player));
        return minutes >= Config.getInteger("values.timeout");
    }

    public static void updateActivity(Player player) {
        activity.put(player, System.currentTimeMillis());
    }
}
