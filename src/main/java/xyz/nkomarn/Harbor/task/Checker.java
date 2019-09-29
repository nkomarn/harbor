package xyz.nkomarn.Harbor.task;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

public class Checker implements Runnable {

    private static final List<World> skippingWorlds = new ArrayList<>();

    @Override
    public void run() {
        Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getWorld).distinct()
                .filter(this::validForCheckWorld)
                .forEach(this::checkWorld);
    }

    private void checkWorld(final World world) {
        final int sleeping = getSleeping(world).size(); // <- 0
        final int needed = getNeeded(world);

        // Send actionbar notification
        if (sleeping > 0 && needed > 0 && Config.getBoolean("messages.actionbar.actionbar")) {
            world.getPlayers().forEach(this::sendActionBar);
        }

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

    private boolean isNight(final World world) {
        return world.getTime() > 12950 || world.getTime() < 23950;
    }

    private void sendActionBar(final Player player) {
        final World world = player.getWorld();
        final String message = Config.getString("messages.actionbar.sleeping");
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                ChatColor.translateAlternateColorCodes('&', message
                        .replace("[sleeping]", String.valueOf(getSleeping(world).size()))
                        .replace("[players]", String.valueOf(world.getPlayers().size()))
                        .replace("[needed]", String.valueOf(getSkipAmount(world)))
                        .replace("[more]", String.valueOf(getNeeded(world))))));
    }

    private List<Player> getSleeping(final World world) {
        return world.getPlayers().stream().filter(Player::isSleeping).collect(toList());
    }

    private int getSkipAmount(final World world) {
        return (int) Math.ceil(getPlayers(world) * (Config.getDouble("values.percent") / 100));
    }

    private int getPlayers(final World world) {
        return Math.max(0, world.getPlayers().size() - getExcluded(world).size());
    }

    private int getNeeded(final World world) {
        return Math.max(0, (int) Math.ceil((getPlayers(world))
                * (Config.getDouble("values.percent") / 100)
                - getSleeping(world).size()));
    }

    private List<Player> getExcluded(final World world) {
        return world.getPlayers().stream().filter(this::isExcluded).collect(toList());
    }

    private boolean isExcluded(final Player p) {
        final boolean excludedByGameMode = Config.getBoolean("features.ignore") && p.getGameMode() != GameMode.SURVIVAL;
        final boolean excludedByPermission = Config.getBoolean("features.bypass") && p.hasPermission("harbor.bypass");
        final boolean excludedByAfk = Harbor.essentials != null && Harbor.essentials.getUser(p).isAfk(); // Essentials AFK detection
        return excludedByGameMode || excludedByPermission || excludedByAfk;
    }

    private String randomMessage(final String list) {
        final List<String> messages = Config.getList(list);
        final Random random = new Random();
        final int index = random.nextInt(messages.size());
        return ChatColor.translateAlternateColorCodes('&', messages.get(index));
    }

    private void sendChatMessage(final String message) {
        if (!Config.getBoolean("messages.chat.chat")) return;
        if (message.length() < 1) return;
        Bukkit.broadcastMessage(message);
    }

    private void accelerateNight(final World world) {
        Bukkit.broadcastMessage(Config.getString("messages.chat.accelerateNight"));

        new BukkitRunnable() {

            @Override
            public void run() {
                final long time = world.getTime();
                if (!(time >= 450 && time <= 1000)) {
                    world.setTime(time + 60);
                } else {
                    // Announce night skip and clear queue
                    sendChatMessage(randomMessage("messages.chat.skipped"));
                    skippingWorlds.remove(world);

                    // Reset sleep statistic if phantoms are disabled
                    if (!Config.getBoolean("features.phantoms")) {
                        world.getPlayers().forEach(player -> player.setStatistic(Statistic.TIME_SINCE_REST, 0));
                    }

                    // Clear weather
                    if (Config.getBoolean("features.weather")) {
                        world.setStorm(false);
                        world.setThundering(false);
                    }

                    this.cancel();
                }
            }

        }.runTaskTimer(Harbor.instance, 20, 1);
    }

}
