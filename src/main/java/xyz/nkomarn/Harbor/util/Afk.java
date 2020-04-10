package xyz.nkomarn.Harbor.util;

import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.Harbor;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Afk {
    private static HashMap<Player, Long> activity = new HashMap<>();

    public static boolean isAfk(Player player) {
        if (!Config.getBoolean("afk-detection.enabled")) return false;

        if (Harbor.getEssentials() != null) {
            return Harbor.getEssentials().getUser(player).isAfk();
        }

        if (Harbor.getOhneemc() != null){
            return Harbor.getOhneemc().api.getAfk(player);
        }

        if (!activity.containsKey(player)) return false;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - activity.get(player));
        return minutes >= Config.getInteger("afk-detection.timeout");
    }

    public static void updateActivity(Player player) {
        activity.put(player, System.currentTimeMillis());
    }
}
