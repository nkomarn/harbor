package xyz.nkomarn.harbor.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.task.Checker;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Messages implements Listener {

    private final Harbor harbor;
    private final Config config;
    private final Random random;

    private final HashMap<UUID, BossBar> bossBars;

    public Messages(@NotNull Harbor harbor) {
        this.harbor = harbor;
        this.config = harbor.getConfiguration();
        this.random = new Random();
        this.bossBars = new HashMap<>();

        for (World world : Bukkit.getWorlds()) {
            if (harbor.getChecker().isBlacklisted(world)) {
                return;
            }

            registerBar(world);
        }
    }

    public void sendWorldChatMessage(@NotNull World world, @NotNull String message) {
        if (!config.getBoolean("messages.chat.enabled") || message.length() < 1) {
            return;
        }

        String preparedMessage = prepareMessage(world, message);
        for (Player player : world.getPlayers()) {
            player.sendMessage(preparedMessage);
        }
    }

    public void sendActionBarMessage(@NotNull World world, @NotNull String message) {
        if (!config.getBoolean("messages.actionbar.enabled") || message.length() < 1) {
            return;
        }

        BaseComponent[] preparedMessage = TextComponent.fromLegacyText(prepareMessage(world, message));
        for (Player player : world.getPlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, preparedMessage);
        }
    }

    public void sendRandomChatMessage(@NotNull World world, @NotNull String listLocation) {
        List<String> messages = config.getStringList(listLocation);

        if (messages.size() < 1) {
            return;
        }

        sendWorldChatMessage(world, messages.get(random.nextInt(Math.max(0, messages.size()))));
    }

    public void sendBossBarMessage(@NotNull World world, @NotNull String message, @NotNull String color, double percentage) {
        if (!config.getBoolean("messages.bossbar.enabled") || message.length() < 1) {
            return;
        }

        BossBar bar = bossBars.get(world.getUID());

        if (bar == null) {
            System.out.println("bar null");
            return;
        }

        if (percentage == 0) {
            bar.removeAll();
            return;
        }

        bar.setTitle(harbor.getMessages().prepareMessage(world, message));
        bar.setColor(getBarColor(color));
        bar.setProgress(percentage);
        world.getPlayers().forEach(bar::addPlayer);
    }

    @NotNull
    private String prepareMessage(final World world, final String message) {
        Checker checker = harbor.getChecker();
        return ChatColor.translateAlternateColorCodes('&', message
                .replace("[sleeping]", String.valueOf(checker.getSleepingPlayers(world).size()))
                .replace("[players]", String.valueOf(checker.getPlayers(world)))
                .replace("[needed]", String.valueOf(checker.getSkipAmount(world)))
                .replace("[more]", String.valueOf(checker.getNeeded(world))));
    }

    @NotNull
    private BarColor getBarColor(final String enumString) {
        BarColor barColor;

        try {
            barColor = BarColor.valueOf(enumString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            barColor = BarColor.BLUE;
        }

        return barColor;
    }

    public void clearBar(@NotNull World world) {
        BossBar bar = bossBars.get(world.getUID());

        if (bar == null) {
            return;
        }

        bar.removeAll();
    }

    private void registerBar(@NotNull World world) {
        BossBar bar = bossBars.get(world.getUID());

        if (bar != null) {
            return;
        }

        bossBars.put(world.getUID(), Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID));
        System.out.println("registered bossbar for world " + world.getName());
    }

    @EventHandler
    public void onWorldLoad(@NotNull WorldLoadEvent event) {
        registerBar(event.getWorld());
    }
}
