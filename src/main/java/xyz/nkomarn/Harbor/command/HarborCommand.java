package xyz.nkomarn.Harbor.command;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HarborCommand implements CommandExecutor {

    int changeTimeTask = 0;

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
        final Player player = (Player) commandSender;
        final World world = player.getWorld();

        //Updater.check();
        //Updater.upgrade();

        return true;
    }
}
