package xyz.nkomarn.Harbor.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Updater;

public class HarborCommand implements CommandExecutor {
    private Config c = new Config();

    @Override
    public boolean onCommand(CommandSender s, Command x, String l, String[] a) {
        if (a.length < 1) {
            s.sendMessage(ChatColor.translateAlternateColorCodes('&', c
                    .getString("messages.miscellaneous.prefix") + "&7Version " + Harbor.version
                    + " by TechToolbox (@nkomarn)."));
            return true;
        }
        if (!s.hasPermission("harbor.admin")) {
            s.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    c.getString("messages.miscellaneous.prefix")
                            + c.getString("messages.miscellaneous.permission")));
            return true;
        }
        if (a[0].equalsIgnoreCase("reload")) {
            try {
                Harbor h = Harbor.instance;
                h.reloadConfig();
                Bukkit.getPluginManager().disablePlugin(h);
                Bukkit.getPluginManager().enablePlugin(h);
                s.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        c.getString("messages.miscellaneous.prefix")
                                + c.getString("messages.miscellaneous.reloaded")));
            }
            catch (Exception e) {
                s.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        c.getString("messages.miscellaneous.prefix")
                                + c.getString("messages.miscellaneous.reloaderror")));
                if (Harbor.debug) e.printStackTrace();
            }
            return true;
        }
        else if (a[0].equalsIgnoreCase("update")) {
            Updater u = new Updater();
            int result = u.upgrade();
            if (result == 0) s.sendMessage(ChatColor.translateAlternateColorCodes('&', c.getString("messages.miscellaneous.prefix") + "&7Harbor was successfully updated to version " + Updater.latest + " and will be enabled after a server restart/reload."));
            else if (result == 1) s.sendMessage(ChatColor.translateAlternateColorCodes('&', c.getString("messages.miscellaneous.prefix") + "&7An error occured while updating Harbor to version " + Updater.latest + ". Check the server console for more details."));
            else if (result == 2) s.sendMessage(ChatColor.translateAlternateColorCodes('&', c.getString("messages.miscellaneous.prefix") + "&7This server is already running the latest version of Harbor. Great work!"));
            return true;
        }
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', c.getString("messages.miscellaneous.prefix") + c.getString("messages.miscellaneous.unrecognized")));
        return true;
    }
}
