package xyz.nkomarn.Harbor.util;

import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.Harbor;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Afk {
    private static final HashMap<Player, Long> ACTIVITY = new HashMap<>();

    public static boolean isAfk(Player player) {
        if (!Config.getBoolean("afk-detection.enabled")) {
            return false;
        } else if (Harbor.getEssentials() != null) {
            return Harbor.getEssentials().getUser(player).isAfk();
        } else {
            if (!ACTIVITY.containsKey(player)) return false;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - ACTIVITY.get(player));
            return minutes >= Config.getInteger("afk-detection.timeout");
        }
    }

    public static void updateActivity(final Player player) {
        ACTIVITY.put(player, System.currentTimeMillis());
    }
}
