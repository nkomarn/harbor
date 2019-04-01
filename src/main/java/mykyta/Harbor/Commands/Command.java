package mykyta.Harbor.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import mykyta.Harbor.Config;
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
            return true;
        }
        else if (args[0].equalsIgnoreCase("update")) {
            return true;
        }
        
        // Unrecognized message if nothing else worked
        
        return true;
    }
}