package techtoolbox.Harbor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Command implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (args.length < 1) {
	        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("messages.miscellaneous.prefix") + "&7Version " + Main.version + " by TechToolbox."));
		}
		else if (args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("harbor.admin")) {
				try {
					Main.bypassers.clear();
					Main.plugin.reloadConfig();
					Bukkit.getServer().getPluginManager().disablePlugin(Main.plugin);
					Bukkit.getServer().getPluginManager().enablePlugin(Main.plugin);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("messages.miscellaneous.prefix") + Main.plugin.getConfig().getString("messages.miscellaneous.reloaded")));
				}
				catch (Exception e) {
		            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("messages.miscellaneous.prefix") + Main.plugin.getConfig().getString("messages.miscellaneous.reloadError")));
		            if (!Main.plugin.getConfig().getBoolean("features.debug")) {
						e.printStackTrace();
					}
				}
			}
			else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("messages.miscellaneous.prefix") + Main.plugin.getConfig().getString("messages.miscellaneous.permission")));
			}
		}
		else if (args[0].equalsIgnoreCase("test")) {
			System.out.println(Main.worlds.size());
			System.out.println(Main.bypassers.size());
		}
	return true;
	}
}
