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
import java.util.concurrent.ThreadLocalRandom;

public class Checker implements Runnable {

    private static List<World> skippingWorlds = new ArrayList<>();

    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {

            // Check for blacklisted worlds
            if (Config.getList("blacklist").contains(world.getName())) return;

            // Check if the night is already being skipped
            if (skippingWorlds.contains(world)) return;

            int sleeping = getSleeping(world).size();
            int needed = getNeeded(world);

            // Send actionbar notification
            if (getSleeping(world).size() > 0 && getNeeded(world) > 0) {
                for (Player player : world.getPlayers()) {
                    sendActionBar(player, Config.getString("messages.actionbar.sleeping"));
                }
            }

            // Check if world is applicable for skipping
            if (Config.getBoolean("features.skip") && getNeeded(world) == 0 && getSleeping(world).size() > 0) {

                // Rapidly accelerate time until it's day
                skippingWorlds.add(world);
                accelerateNight(world);

            }
        }
    }

    private void sendActionBar(Player player, String message) {
        World world = player.getWorld();

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                ChatColor.translateAlternateColorCodes('&', message
                    .replace("[sleeping]", String.valueOf(getSleeping(world)))
                    .replace("[players]", String.valueOf(world.getPlayers().size()))
                    .replace("[needed]", String.valueOf(getSkipAmount(world)))
                    .replace("[more]", String.valueOf(getNeeded(world))))));
    }

    private List<Player> getSleeping(World world) {
        List<Player> sleeping = new ArrayList<>();
        for (Player player : world.getPlayers()) {
            if (player.isSleeping()) sleeping.add(player);
        }
        return sleeping;
    }

    private int getSkipAmount(World world) {
        return (int) (getPlayers(world) * (Config.getDouble("values.percent") / 100));
    }

    private int getPlayers(World world) {
        return Math.max(0, world.getPlayers().size() - getExcluded(world).size());
    }


    private int getNeeded(World world) {
        return Math.max(0, (int) Math.ceil((getPlayers(world))
            * (Config.getDouble("values.percent") / 100)
            - getSleeping(world).size()));
    }

    private ArrayList<Player> getExcluded(World w) {
        ArrayList<Player> a = new ArrayList<>();
        w.getPlayers().forEach(p -> {
            if (isExcluded(p)) a.add(p);
        });
        return a;
    }

    private boolean isExcluded(Player p) {
        boolean s = false;
        if (Config.getBoolean("features.ignore")) if (p.getGameMode() == GameMode.SURVIVAL) s = false; else s = true;
        if (Config.getBoolean("features.bypass")) if (p.hasPermission("harbor.bypass")) s = true; else s = false;

        // Essentials AFK detection
        if (Harbor.essentials != null) {
            if (Harbor.essentials.getUser(p).isAfk()) s = true;
        }

        return s;
    }

    private String randomMessage(String list) {
        List<String> messages = Config.getList(list);
        Random random = new Random();
        int index = random.nextInt(messages.size());
        return ChatColor.translateAlternateColorCodes('&', messages.get(index));
    }

    private void sendChatMessage(String message) {
        if (!Config.getBoolean("messages.chat.chat")) return;
        if (message.length() < 1) return;
        Bukkit.broadcastMessage(message);
    }

    private void accelerateNight(World world) {
        Bukkit.broadcastMessage("Harbor - Accelerating time.");

        new BukkitRunnable() {

            @Override
            public void run() {
                long time = world.getTime();
                if (!(time >= 450 && time <= 1000)) {
                    world.setTime(time + 60);
                }
                else {

                    // Announce night skip and clear queue
                    sendChatMessage("messages.chat.skipped");
                    skippingWorlds.remove(world);

                    // Reset sleep statistic if phantoms are disabled
                    if (!Config.getBoolean("features.phantoms")) {
                        for (Player player : world.getPlayers()) {
                            player.setStatistic(Statistic.TIME_SINCE_REST, 0);
                        }
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
