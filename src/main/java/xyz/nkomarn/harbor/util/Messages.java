package xyz.nkomarn.harbor.util;

import com.google.common.base.Enums;
import me.clip.placeholderapi.PlaceholderAPI;
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
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.task.Checker;

import java.util.*;

public class Messages implements Listener {

    private final Harbor harbor;
    private final Config config;
    private final Random random;
    private final HashMap<UUID, BossBar> bossBars;
    private final boolean papiPresent;

    public Messages(@NotNull Harbor harbor) {
        this.harbor = harbor;
        this.config = harbor.getConfiguration();
        this.random = new Random();
        this.bossBars = new HashMap<>();
        this.papiPresent = harbor.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        for (World world : Bukkit.getWorlds()) {
            if (!harbor.getChecker().isBlacklisted(world)) {
                registerBar(world);
            }
        }
    }

    public void sendWorldChatMessage(@NotNull World world, @NotNull String message) {
        if (!config.getBoolean("messages.chat.enabled") || message.isEmpty()) {
            return;
        }

        String preparedMessage = prepareMessage(world, message);
        for (World targetWorld : harbor.getChecker().getSyncedWorlds(world)) {
            for (Player player : targetWorld.getPlayers()) {
                player.sendMessage(preparedMessage);
            }
        }
    }

    public void sendActionBarMessage(@NotNull World world, @NotNull String message) {
        if (!config.getBoolean("messages.actionbar.enabled") || message.isEmpty()) {
            return;
        }

        BaseComponent[] preparedMessage = TextComponent.fromLegacyText(prepareMessage(world, message));
        for (World targetWorld : harbor.getChecker().getSyncedWorlds(world)) {
            for (Player player : targetWorld.getPlayers()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, preparedMessage);
            }
        }
    }

    public void sendRandomChatMessage(@NotNull World world, @NotNull String listLocation) {
        List<String> messages = config.getStringList(listLocation);
        if (messages.isEmpty()) {
            return;
        }

        sendWorldChatMessage(world, messages.get(random.nextInt(messages.size())));
    }

    public void sendBossBarMessage(@NotNull World world, @NotNull String message, @NotNull String color, double percentage) {
        if (!config.getBoolean("messages.bossbar.enabled") || message.isEmpty()) {
            return;
        }

        World rep = harbor.getChecker().getRepresentativeWorld(world);
        BossBar bar = bossBars.get(rep.getUID());
        if (bar == null) {
            return;
        }

        if (percentage == 0) {
            bar.removeAll();
            return;
        }

        bar.setTitle(prepareMessage(rep, message));
        bar.setColor(Enums.getIfPresent(BarColor.class, color).or(BarColor.BLUE));
        bar.setProgress(percentage);

        Set<Player> activePlayers = new HashSet<>();
        for (World targetWorld : harbor.getChecker().getSyncedWorlds(rep)) {
            activePlayers.addAll(targetWorld.getPlayers());
        }

        for (Player current : new ArrayList<>(bar.getPlayers())) {
            if (!activePlayers.contains(current)) {
                bar.removePlayer(current);
            }
        }
        activePlayers.forEach(bar::addPlayer);
    }

    @NotNull
    public String prepareMessage(@NotNull World world, @NotNull String message) {
        Checker checker = harbor.getChecker();
        return ChatColor.translateAlternateColorCodes('&', message
                .replace("[sleeping]", String.valueOf(checker.getSleepingPlayers(world).size()))
                .replace("[players]", String.valueOf(checker.getPlayers(world)))
                .replace("[needed]", String.valueOf(checker.getSkipAmount(world)))
                .replace("[more]", String.valueOf(checker.getNeeded(world))));
    }

    @NotNull
    public String prepareMessage(@NotNull Player player, @NotNull String message) {
        String output = ChatColor.translateAlternateColorCodes('&', message
                .replace("[player]", player.getName())
                .replace("[displayname]", player.getDisplayName()));

        if (papiPresent) {
            output = PlaceholderAPI.setPlaceholders(player, output);
        }

        return output;
    }

    private void registerBar(@NotNull World world) {
        bossBars.computeIfAbsent(world.getUID(), uuid -> Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID));
    }

    public void clearBar(@NotNull World world) {
        for (World targetWorld : harbor.getChecker().getSyncedWorlds(world)) {
            Optional.ofNullable(bossBars.get(targetWorld.getUID())).ifPresent(BossBar::removeAll);
        }
    }

    @EventHandler
    public void onWorldLoad(@NotNull WorldLoadEvent event) {
        if (!harbor.getChecker().isBlacklisted(event.getWorld())) {
            registerBar(event.getWorld());
        }
    }

    @EventHandler
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        World fromRep = harbor.getChecker().getRepresentativeWorld(event.getFrom());
        World toRep = harbor.getChecker().getRepresentativeWorld(event.getPlayer().getWorld());
        if (!fromRep.getUID().equals(toRep.getUID())) {
            Optional.ofNullable(bossBars.get(fromRep.getUID())).ifPresent(bossBar -> bossBar.removePlayer(event.getPlayer()));
        }
    }
}
