package xyz.nkomarn.Harbor.command;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.util.Updater;

public class HarborCommand implements CommandExecutor {

    int changeTimeTask = 0;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        World world = player.getWorld();

        Updater.check();
        Updater.upgrade();

        return true;
    }
}
