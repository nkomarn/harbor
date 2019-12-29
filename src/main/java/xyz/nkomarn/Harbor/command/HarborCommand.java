package xyz.nkomarn.Harbor.command;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.task.AccelerateNightTask;
import xyz.nkomarn.Harbor.task.Checker;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Updater;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HarborCommand implements TabExecutor {
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
        else if (args[0].equalsIgnoreCase("forceskip")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                        + "&7This command requires you to be a player."));
                return true;
            }

            Player player = (Player) sender;
            World world = player.getWorld();

            Checker.skippingWorlds.add(world);
            new AccelerateNightTask(world).runTaskTimer(Harbor.instance, 0L, 1);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                    + "&7Forcing night skip in your world."));
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
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&7You're already running "
                    + "the latest version of Harbor. Great work!"));
            }
            return true;
        }

        // Otherwise, send unrecognized argument messages
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                + Config.getString("messages.miscellaneous.unrecognized")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("harbor.admin")) return null;
        if (args.length != 1) return null;
        return Arrays.asList("forceskip", "reload", "update");
    }
}
