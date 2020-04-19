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
        Messages.sendRandomChatMessage(world, "messages.chat.night-skipping");

        Bukkit.getScheduler().runTask(Harbor.getHarbor(), () -> {
            if (Config.getBoolean("night-skip.clear-rain")) {
                world.setStorm(false);
            }

            if (Config.getBoolean("night-skip.clear-thunder")) {
                world.setThundering(false);
            }
        });
    }

    @Override
    public void run() {
        final long time = world.getTime();
        final int dayTime = Math.max(150, Config.getInteger("night-skip.daytime-ticks"));
        final int sleeping = Checker.getSleeping(world).size();
        double timeRate = Config.getInteger("night-skip.time-rate");

        if (Config.getBoolean("night-skip.proportional-acceleration")) {
            if (sleeping != 0) timeRate = Math.min(timeRate, Math.round(timeRate /
                    world.getPlayers().size() * sleeping));
        }

        if (time >= (dayTime - timeRate * 1.5) && time <= dayTime) {
            if (Config.getBoolean("night-skip.reset-phantom-statistic")) {
                world.getPlayers().forEach(player -> player.setStatistic(Statistic.TIME_SINCE_REST, 0));
            }

            Checker.skippingWorlds.remove(world);
            Messages.sendRandomChatMessage(world, "messages.chat.night-skipped");
            this.cancel();
        } else {
            world.setTime(time + (int) timeRate);
        }
    }
}
