package xyz.nkomarn.Harbor;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.Harbor.command.HarborCommand;
import xyz.nkomarn.Harbor.listener.AfkListener;
import xyz.nkomarn.Harbor.listener.BedListener;
import xyz.nkomarn.Harbor.task.Checker;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Metrics;

public class Harbor extends JavaPlugin {
    public static Harbor instance;
    public static String version = "1.6.2";
    public static Essentials essentials;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this,
                new Checker(), 0L, Config.getInteger("values.timer") * 20);

        final PluginManager pluginManager = getServer().getPluginManager();
        getCommand("harbor").setExecutor(new HarborCommand());
        pluginManager.registerEvents(new BedListener(), this);

        if (!Config.getString("version").equals(version)) {
            getLogger().warning(String.format("The configuration version is '%s' but you're running Harbor " +
                    "version '%s'. Please update your configuration.", Config.getString("version"), version));
        }

        // bStats
        new Metrics(this);

        // Essentials hook
        essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");

        // If Essentials isn't present, enable fallback AFK system
        if (essentials == null) {
            getLogger().info("Essentials not present- registering fallback AFK detection system.");
            getServer().getPluginManager().registerEvents(new AfkListener(), this);
        }
    }
}
