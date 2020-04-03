package xyz.nkomarn.Harbor.command;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.task.AccelerateNightTask;
import xyz.nkomarn.Harbor.task.Checker;
import xyz.nkomarn.Harbor.util.Config;

import java.util.Arrays;
import java.util.List;

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

        if (args[0].equalsIgnoreCase("reload")) {
            Harbor.getHarbor().reloadConfig();
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

            if (Checker.skippingWorlds.contains(world)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                    + "&7This world's time is already being accelerated."));
                return true;
            }

            Checker.skippingWorlds.add(world);
            new AccelerateNightTask(world).runTaskTimer(Harbor.getHarbor(), 0L, 1);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                    + "&7Forcing night skip in your world."));
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
