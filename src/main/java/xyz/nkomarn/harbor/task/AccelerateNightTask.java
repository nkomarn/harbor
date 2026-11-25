package xyz.nkomarn.harbor.task;

import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.util.Config;

import java.util.List;

public class AccelerateNightTask extends BukkitRunnable {

    private final Harbor harbor;
    private final Checker checker;
    private final World masterWorld;

    public AccelerateNightTask(@NotNull Harbor harbor, @NotNull Checker checker, @NotNull World world) {
        this.harbor = harbor;
        this.checker = checker;
        this.masterWorld = checker.getRepresentativeWorld(world);

        harbor.getMessages().sendRandomChatMessage(masterWorld, "messages.chat.night-skipping");
        checker.clearWeather(masterWorld);
        runTaskTimer(harbor, 1, 1);
    }

    @Override
    public void run() {
        Config config = harbor.getConfiguration();
        List<World> syncedWorlds = checker.getSyncedWorlds(masterWorld);

        long time = masterWorld.getTime();
        double timeRate = config.getInteger("night-skip.time-rate");
        int dayTime = Math.floorMod(config.getInteger("night-skip.daytime-ticks"), 24000);
        int sleeping = checker.getSleepingPlayers(masterWorld).size();

        if (config.getBoolean("night-skip.proportional-acceleration")) {
            int totalPlayers = syncedWorlds.stream().mapToInt(w -> w.getPlayers().size()).sum();
            if (totalPlayers > 0) {
                timeRate = Math.min(timeRate, Math.round(timeRate / totalPlayers * Math.max(1, sleeping)));
            }
        }

        int step = Math.max(1, (int) timeRate);
        long distanceToTarget = Math.floorMod(dayTime - time, 24000L);

        if (distanceToTarget <= step) {
            syncedWorlds.forEach(w -> w.setTime(dayTime));

            if (config.getBoolean("night-skip.reset-phantom-statistic")) {
                syncedWorlds.forEach(w -> w.getPlayers()
                        .forEach(player -> player.setStatistic(Statistic.TIME_SINCE_REST, 0)));
            }

            checker.resetStatus(masterWorld);
            cancel();
            return;
        }

        long nextTime = (time + step) % 24000L;
        syncedWorlds.forEach(w -> w.setTime(nextTime));
    }
}
