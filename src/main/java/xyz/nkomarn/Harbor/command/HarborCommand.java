package xyz.nkomarn.Harbor.command;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Updater;

import java.util.concurrent.ExecutionException;

public class HarborCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

        final String prefix = Config.getString("messages.miscellaneous.prefix");

        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&7Harbor version "
                    + Harbor.version + " by TechToolbox (@nkomarn)."));
            return true;
        }

        if (!sender.hasPermission("harbor.admin")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                    + Config.getString("messages.miscellaneous.permission")));
            return true;
        }
        else if (args[0].equalsIgnoreCase("reload")) {
            Harbor.instance.reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                    + "&7Reloaded configuration."));
            return true;
        }
        else if (args[0].equalsIgnoreCase("update")) {

            // Fancy actionbar stuff
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                        prefix + "&fChecking for updates.")));
            }

            boolean updateAvailable;
            try {
                updateAvailable = Updater.check().get();
                System.out.println(updateAvailable);
            } catch (ExecutionException | InterruptedException e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&7Failed to check for a "
                    + "new update. Check console for full log."));
                e.printStackTrace();
                return true;
            }

            if (updateAvailable) {
                try {
                    // More fancy actionbar stuff
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                                prefix + "&fUpdate found, upgrading.")));
                    }

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&7"
                        + Updater.upgrade().get()));
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&7You're already running "
                    + "the latest version of Harbor. Great work!"));
                return true;
            }
        }

        // Otherwise, send unrecognized argument messages
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                + Config.getString("messages.miscellaneous.unrecognized")));
        return true;
    }
}
