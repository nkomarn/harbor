package xyz.nkomarn.Harbor.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.task.Checker;

import java.util.List;
import java.util.Random;

public class Messages {

    public static void sendRandomChatMessage(final World world, final String messageList) {
        final List<String> messages = Config.getList(messageList);
        final int index = new Random().nextInt(messages.size());
        sendWorldChatMessage(world, messages.get(index));
    }

    private static void sendPlayerChatMessage(final Player player, final String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    private static void sendWorldChatMessage(final World world, final String message) {
        world.getPlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public static void sendActionBarMessage(final Player player, final String message) {
        final World world = player.getWorld();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                ChatColor.translateAlternateColorCodes('&', message
                        .replace("[sleeping]", String.valueOf(Checker.getSleeping(world).size()))
                        .replace("[players]", String.valueOf(Checker.getPlayers(world)))
                        .replace("[needed]", String.valueOf(Checker.getSkipAmount(world)))
                        .replace("[more]", String.valueOf(Checker.getNeeded(world))))));
    }

}
