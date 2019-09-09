package xyz.nkomarn.Harbor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.Harbor.command.HarborCommand;
import xyz.nkomarn.Harbor.task.Checker;
import xyz.nkomarn.Harbor.util.Config;

public class Harbor extends JavaPlugin {

    public static Harbor instance;
    public static boolean debug = false;

    public void onEnable() {
        instance = this;
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this,
                new Checker(), 0L, Config.getInteger("values.check") * 20);

        getCommand("harbor").setExecutor(new HarborCommand());
    }

    public void onDisable() {

    }

}
