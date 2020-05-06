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
        final String prefix = Config.getString("messages.miscellaneous.chat-prefix");

        if (args.length < 1 || !sender.hasPermission("harbor.admin")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "%sHarbor version %s by TechToolbox (@nkomarn).", prefix, Harbor.version)));
        } else if (args[0].equalsIgnoreCase("reload")) {
            Harbor.getHarbor().reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                    + "Reloaded configuration."));
        } else if (args[0].equalsIgnoreCase("forceskip")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                        + "This command requires you to be a player."));
            } else {
                Player player = (Player) sender;
                World world = player.getWorld();

                if (Checker.SKIPPING_WORLDS.contains(world)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                            + "This world's time is already being accelerated."));
                } else {
                    Checker.SKIPPING_WORLDS.add(world);
                    new AccelerateNightTask(world).runTaskTimer(Harbor.getHarbor(), 0L, 1);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                            + "Forcing night skip in your world."));
                }
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                    + Config.getString("messages.miscellaneous.unrecognized-command")));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("harbor.admin")) return null;
        if (args.length != 1) return null;
        return Arrays.asList("forceskip", "reload");
    }
}
