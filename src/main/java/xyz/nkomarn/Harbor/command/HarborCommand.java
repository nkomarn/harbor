package xyz.nkomarn.Harbor.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.util.Updater;

public class HarborCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0]) && sender.hasPermission("harbor.reload")) {
            Harbor.instance.reloadConfig();
            sender.sendMessage("§1[Harbor]: §2 Reloaded");
        }
        return true;
    }

    private void checkForUpdate() {
        Updater.check();
        Updater.upgrade();
    }
}
