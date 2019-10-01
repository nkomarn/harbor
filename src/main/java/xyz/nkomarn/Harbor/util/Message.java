package xyz.nkomarn.Harbor.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.task.Checker;

import java.util.List;
import java.util.Random;

public class Message {
    public static void SendChatMessage(final World world, final String messageLocation, final String playerName, final int change) {
        sendChatMessage(prepareMessageWithParams(Config.getString(messageLocation), world, playerName, change));
    }

    public static void SendActionbarMessage(final World world, final String messageLocation, final String playerName, final int change) {
        if (Config.getBoolean("messages.actionbar.actionbar")) {
            final String message = prepareMessageWithParams(Config.getString(messageLocation), world, playerName, change);
            world.getPlayers().forEach(p -> sendActionbarMessage(p, message));
        }
    }

    public static void SendRandomChatMessage(final String messageListLocation) {
        final List<String> messages = Config.getList(messageListLocation);
        final int index = new Random().nextInt(messages.size());
        sendChatMessage(messages.get(index));
    }

    private static void sendChatMessage(final String message) {
        if (Config.getBoolean("messages.chat.chat") && message.length() > 0) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    private static void sendActionbarMessage(final Player player, final String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    private static int getSkipAmount(final World world) {
        return (int) Math.ceil(Checker.getPlayers(world) * (Config.getDouble("values.percent") / 100));
    }

    private static String prepareMessageWithParams(final String message, final World world, final String playerName, final int change) {
        return ChatColor.translateAlternateColorCodes('&', message
                .replace("[sleeping]", String.valueOf(Checker.getSleeping(world) + change))
                .replace("[needed]", String.valueOf(getSkipAmount(world)))
                .replace("[more]", String.valueOf(Checker.getNeeded(world) - change))
                .replace("[player]", playerName));
    }
}
