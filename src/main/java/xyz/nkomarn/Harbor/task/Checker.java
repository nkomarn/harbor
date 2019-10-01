package xyz.nkomarn.Harbor.task;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Message;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Checker implements Runnable {

    public static final List<World> skippingWorlds = new ArrayList<>();

    @Override
    public void run() {
        Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getWorld).distinct()
                .filter(this::validForCheckWorld)
                .forEach(this::checkWorld);
    }

    public static int getSleeping(final World world) {
        return world.getPlayers().stream().filter(Player::isSleeping).collect(toList()).size();
    }

    public static int getNeeded(final World world) {
        return Math.max(0, (int) Math.ceil((getPlayers(world))
                * (Config.getDouble("values.percent") / 100)
                - Checker.getSleeping(world)));
    }

    public static int getPlayers(final World world) {
        return Math.max(0, world.getPlayers().size() - getExcluded(world).size());
    }

    public static boolean isNight(final World world) {
        return world.getTime() > 12950 || world.getTime() < 23950;
    }

    private static List<Player> getExcluded(final World world) {
        return world.getPlayers().stream().filter(Checker::isExcluded).collect(toList());
    }

    private static boolean isExcluded(final Player p) {
        final boolean excludedByGameMode = Config.getBoolean("features.ignore") && p.getGameMode() != GameMode.SURVIVAL;
        final boolean excludedByPermission = Config.getBoolean("features.bypass") && p.hasPermission("harbor.bypass");
        final boolean excludedByAfk = Harbor.essentials != null && Harbor.essentials.getUser(p).isAfk(); // Essentials AFK detection
        return excludedByGameMode || excludedByPermission || excludedByAfk;
    }

    private void checkWorld(final World world) {
        final int sleeping = getSleeping(world);
        final int needed = getNeeded(world);

        // Check if world is applicable for skipping
        if (needed == 0 && sleeping > 0) {
            // Rapidly accelerate time until it's day
            skippingWorlds.add(world);
            accelerateNight(world);
        }
    }

    private boolean validForCheckWorld(final World world) {
        return notBlacklisted(world)
                && isNight(world)
                && !skippingWorlds.contains(world);
    }

    private boolean notBlacklisted(final World world) {
        return !Config.getList("blacklist").contains(world.getName());
    }

    private void accelerateNight(final World world) {
        Message.SendChatMessage(world, "messages.chat.accelerateNight", "", 0);
        Message.SendActionbarMessage(world, "messages.actionbar.everyone", "", 0);
        new AccelerateNightTask(world).runTaskTimer(Harbor.instance, 0L, 1);
    }
}
