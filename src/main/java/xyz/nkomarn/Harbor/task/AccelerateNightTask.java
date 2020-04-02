package xyz.nkomarn.Harbor.task;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Messages;

public class AccelerateNightTask extends BukkitRunnable {
    private final World world;

    public AccelerateNightTask(final World world) {
        this.world = world;
    }

    @Override
    public void run() {
        final long time = world.getTime();
        final int interval = Config.getInteger("values.interval");

        // Variable interval based on player count

        if (!(time >= 450 && time <= 1000)) {
            world.setTime(time + interval);
        } else {
            if (!Config.getBoolean("features.phantoms")) {
                world.getPlayers().forEach(player ->
                        player.setStatistic(Statistic.TIME_SINCE_REST, 0));
            }

            if (Config.getBoolean("features.clear-rain")) {
                world.setStorm(false);
            }

            if (Config.getBoolean("features.clear-thunder")) {
                world.setThundering(false);
            }

            Checker.skippingWorlds.remove(world);
            Messages.sendRandomChatMessage(world, "messages.chat.skipped");
            this.cancel();
        }
    }
}
