package xyz.nkomarn.Harbor;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.Harbor.command.HarborCommand;
import xyz.nkomarn.Harbor.listener.PlayerListener;
import xyz.nkomarn.Harbor.task.Checker;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Metrics;

public class Harbor extends JavaPlugin {

    public static Harbor instance;
    public static String version = "1.6";
    public static Essentials essentials;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        if (Config.getBoolean("features.skip")) {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this,
                    new Checker(), 0L, Config.getInteger("values.timer") * 20);
        }

        getCommand("harbor").setExecutor(new HarborCommand());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // bStats
        final Metrics metrics = new Metrics(this);

        // Essentials hook
        essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
    }
}
