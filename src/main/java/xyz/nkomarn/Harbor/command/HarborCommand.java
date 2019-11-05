package xyz.nkomarn.Harbor.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Updater;

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
            if (Updater.check()) {
                // TODO
            }
        }

        // Otherwise, send unrecognized argument messages
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
                + Config.getString("messages.miscellaneous.unrecognized")));
        return true;

        /*if (args.length == 1 && "reload".equalsIgnoreCase(args[0]) && sender.hasPermission("harbor.reload")) {
            Harbor.instance.reloadConfig();
            sender.sendMessage("§1[Harbor]: §2 Reloaded");
        }*/
    }

    private void checkForUpdate() {
        Updater.check();
        Updater.upgrade();
    }
}
