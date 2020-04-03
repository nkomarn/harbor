package xyz.nkomarn.Harbor.task;

import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Messages;

public class AccelerateNightTask extends BukkitRunnable {
    private final World world;

    public AccelerateNightTask(final World world) {
        this.world = world;
        Messages.sendRandomChatMessage(world, "messages.chat.night-skipping");
    }

    @Override
    public void run() {
        final long time = world.getTime();
        final int dayTime = Config.getInteger("night-skip.daytime-ticks");
        final int timeRate = Config.getInteger("night-skip.time-rate");

        if (time >= (dayTime - timeRate * 2) && time <= dayTime) {
            if (Config.getBoolean("features.clear-rain")) {
                world.setStorm(false);
            }

            if (Config.getBoolean("features.clear-thunder")) {
                world.setThundering(false);
            }

            if (Config.getBoolean("night-skip.reset-phantom-statistic")) {
                world.getPlayers().forEach(player -> player.setStatistic(Statistic.TIME_SINCE_REST, 0));
            }

            Checker.skippingWorlds.remove(world);
            Messages.sendRandomChatMessage(world, "messages.chat.night-skipped");
            this.cancel();
        } else {
            world.setTime(time + timeRate);
        }
    }
}
