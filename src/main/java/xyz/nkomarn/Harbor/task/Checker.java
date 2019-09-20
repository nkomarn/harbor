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
                    sendActionBar(player, Config.getString("messages.actionbar.sleeping")
                        .replace("[sleeping]", String.valueOf(sleeping))
                        .replace("[online]", String.valueOf(world.getPlayers().size()))
                        .replace("[needed]", String.valueOf(needed)));
                }
            }

            // Check if world is applicable for skipping
            if (getNeeded(world) == 0 && getSleeping(world).size() > 0) {

                // Rapidly accelerate time until it's day
                skippingWorlds.add(world);
                accelerateNight(world);

            }
        }
    }

    private void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                ChatColor.translateAlternateColorCodes('&', message)));
    }

    private List<Player> getSleeping(World world) {
        List<Player> sleeping = new ArrayList<>();
        for (Player player : world.getPlayers()) {
            if (player.isSleeping()) sleeping.add(player);
        }
        return sleeping;
    }

    private int getNeeded(World world) {
        try {
            return Math.max(0, (int) Math.ceil((world.getPlayers().size()
                    - getExcluded(world).size()) * (Config.getDouble("values.percent") / 100)
                    - getSleeping(world).size()));}
        catch (NullPointerException e) {
            if (Harbor.debug) e.printStackTrace();
            return 0;
        }
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
                    Bukkit.broadcastMessage("Harbor - Stopped time change (" + time + ").");
                    skippingWorlds.remove(world);

                    // Reset sleep statistic if phantoms are disabled TODO move out of here
                    if (!Config.getBoolean("features.phantoms")) {
                        for (Player player : world.getPlayers()) {
                            player.setStatistic(Statistic.TIME_SINCE_REST, 0);
                        }
                    }

                    this.cancel();
                }
            }

        }.runTaskTimer(Harbor.instance, 20, 1);
    }

}
