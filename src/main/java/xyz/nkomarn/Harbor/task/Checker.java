package xyz.nkomarn.Harbor.task;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Messages;

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
            .filter(this::validateWorld)
            .forEach(this::checkWorld);
    }

    private void checkWorld(final World world) {
        final int sleeping = getSleeping(world).size();
        final int needed = getNeeded(world);

        // Send actionbar sleeping notification
        if (sleeping > 0 && needed > 0) {
            world.getPlayers().forEach(player -> Messages.sendActionBarMessage(player,
                    Config.getString("messages.actionbar.sleeping")));
        } else if (needed == 0 && sleeping > 0) {
            world.getPlayers().forEach(player -> Messages.sendActionBarMessage(player, 
                Config.getString("messages.actionbar.everyone")));
            skippingWorlds.add(world);
            new AccelerateNightTask(world).runTaskTimer(Harbor.instance, 0L, 1);
            Messages.sendRandomChatMessage(world, "messages.chat.accelerateNight");
        }
    }

    private boolean validateWorld(final World world) {
        return !isBlacklisted(world)
            && !skippingWorlds.contains(world) 
            && isNight(world);
    }

    private boolean isBlacklisted(final World world) {
        return Config.getList("blacklist").contains(world.getName());
    }

    private boolean isNight(final World world) {
        return world.getTime() > 12950 || world.getTime() < 23950;
    }
    
    public static List<Player> getSleeping(final World world) {
        return world.getPlayers().stream().filter(Player::isSleeping).collect(toList());
    }

    public static int getSkipAmount(final World world) {
        return (int) Math.ceil(getPlayers(world) * (Config.getDouble("values.percent") / 100));
    }

    public static int getPlayers(final World world) {
        return Math.max(0, world.getPlayers().size() - getExcluded(world).size());
    }

    public static int getNeeded(final World world) {
        return Math.max(0, (int) Math.ceil((getPlayers(world))
                * (Config.getDouble("values.percent") / 100)
                - getSleeping(world).size()));
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

}
