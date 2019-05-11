package mykyta.Harbor.Commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import mykyta.Harbor.Config;
import mykyta.Harbor.Harbor;
import mykyta.Harbor.Updater;
import mykyta.Harbor.Util;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Config config = new Config();
        Util util = new Util();

        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + "&7Version " + util.version + " by TechToolbox."));
            return true;
        }

        // Permission check
        if (!sender.hasPermission("harbor.admin")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + config.getString("messages.miscellaneous.permission")));
            return true;
        }

        // Command arguments
        if (args[0].equalsIgnoreCase("reload")) {
            try {
                Harbor h = Config.harbor;
                h.reloadConfig();
                Bukkit.getPluginManager().disablePlugin(h);
                Bukkit.getPluginManager().enablePlugin(h);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + config.getString("messages.miscellaneous.reloaded")));
            }
            catch (Exception e) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + config.getString("messages.miscellaneous.reloaderror")));
                if (Util.debug) e.printStackTrace();
            }
            return true;
        } 
        else if (args[0].equalsIgnoreCase("update")) {
            Updater u = new Updater();
            int result = u.upgrade();
            if (result == 0) sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + "&7Harbor was successfully updated to version " + Updater.latest + " and will be enabled after a server restart/reload."));
            else if (result == 1) sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + "&7An error occured while updating Harbor to version " + Updater.latest + ". Check the server console for more details."));
            else if (result == 2) sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + "&7This server is already running the latest version of Harbor. Great work!"));
            return true;
        }

        // Unrecognized message if nothing else worked
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + config.getString("messages.miscellaneous.unrecognized")));
        return true;
    }
}