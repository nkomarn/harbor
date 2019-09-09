package xyz.nkomarn.Harbor.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nkomarn.Harbor.Harbor;

public class HarborCommand implements CommandExecutor {

    int changeTimeTask = 0;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        World world = player.getWorld();

        changeTimeTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Harbor.instance, new Runnable() {

            @Override
            public void run() {
                if (!(world.getTime() >= 450 && world.getTime() <= 1000)) {
                    world.setTime(world.getTime() + 60);
                }
                else {
                    Bukkit.getScheduler().cancelTask(changeTimeTask);
                    System.out.println("Stopped time change");
                }
            }
        }, 0L, 1L);
        return true;
    }
}
