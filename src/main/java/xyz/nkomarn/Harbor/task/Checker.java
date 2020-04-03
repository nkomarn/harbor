package xyz.nkomarn.Harbor.task;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import org.bukkit.metadata.MetadataValue;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.util.Afk;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Messages;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Checker implements Runnable {
    public static final List<World> skippingWorlds = new ArrayList<>();

    @Override
    public void run() {
        Bukkit.getWorlds().stream().filter(this::validateWorld).forEach(this::checkWorld);
    }

    private void checkWorld(final World world) {
        final int sleeping = getSleeping(world).size();
        final int needed = getNeeded(world);

        if (sleeping > 0 && needed > 0) {
            // TODO redo bossbars
            final double sleepingPercentage = Math.min(1, (double) sleeping / getSkipAmount(world));
            Messages.sendBossBarMessage(world, Config.getString("messages.bossbar.players-sleeping.message"),
                    BarColor.valueOf(Config.getString("messages.bossbar.players-sleeping.color")), sleepingPercentage);


            Messages.sendActionBarMessage(world, Config.getString("messages.actionbar.players-sleeping"));
        } else if (needed == 0 && sleeping > 0) {
            Messages.sendBossBarMessage(world, Config.getString("messages.bossbar.night-skipping.message"),
                    BarColor.valueOf(Config.getString("messages.bossbar.night-skipping.color")), 1);

            Messages.sendActionBarMessage(world, Config.getString("messages.actionbar.night-skipping"));

            if (!Config.getBoolean("night-skip.enabled")) return;

            if (Config.getBoolean("night-skip.instant-skip")) {
                Bukkit.getScheduler().runTask(Harbor.getHarbor(), () ->
                        world.setTime(Config.getInteger("night-skip.daytime-ticks")));
                Messages.sendRandomChatMessage(world, "messages.chat.night-skipped");
            } else {
                skippingWorlds.add(world);
                new AccelerateNightTask(world).runTaskTimer(Harbor.getHarbor(), 1, 1);
            }
        }
    }

    private boolean validateWorld(final World world) {
        return !isBlacklisted(world)
            && !skippingWorlds.contains(world) 
            && isNight(world);
    }

    private boolean isBlacklisted(final World world) {
        return Config.getList("blacklisted-worlds").contains(world.getName());
    }

    private boolean isNight(final World world) {
        return world.getTime() > 12950 || world.getTime() < 23950;
    }
    
    public static List<Player> getSleeping(final World world) {
        return world.getPlayers().stream().filter(Player::isSleeping).collect(toList());
    }

    public static int getSkipAmount(final World world) {
        return (int) Math.ceil(getPlayers(world) * (Config.getDouble("night-skip.percentage") / 100));
    }

    public static int getPlayers(final World world) {
        return Math.max(0, world.getPlayers().size() - getExcluded(world).size());
    }

    public static int getNeeded(final World world) {
        return Math.max(0, (int) Math.ceil((getPlayers(world))
                * (Config.getDouble("night-skip.percentage") / 100)
                - getSleeping(world).size()));
    }

    private static List<Player> getExcluded(final World world) {
        return world.getPlayers().stream().filter(Checker::isExcluded).collect(toList());
    }

    private static boolean isExcluded(final Player player) {
        final boolean excludedByCreative = Config.getBoolean("exclusions.exclude-creative") && player.getGameMode() == GameMode.CREATIVE;
        final boolean excludedBySpectator = Config.getBoolean("exclusions.exclude-spectator") && player.getGameMode() == GameMode.SPECTATOR;
        final boolean excludedByPermission = Config.getBoolean("exclusions.bypass-permission") && player.hasPermission("harbor.ignored");
        final boolean excludedByAfk = Afk.isAfk(player);

        if (Config.getBoolean("exclusions.exclude-vanished")) {
            for (MetadataValue meta : player.getMetadata("vanished")) if (meta.asBoolean()) return true;
        }
        return excludedByCreative || excludedBySpectator || excludedByPermission || excludedByAfk || player.isSleepingIgnored();
    }
}
