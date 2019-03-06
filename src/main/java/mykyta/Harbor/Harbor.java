package mykyta.Harbor;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import mykyta.Harbor.Events.BedEnter;

public class Harbor extends JavaPlugin {
    private Logger log = Bukkit.getLogger();
    private Updater updater = new Updater();

    public void onEnable() {
        Config config = new Config();
        Util util = new Util();
        config.setInstance(this);
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new BedEnter(), this);
        
        util.setupNMS();

        // Check for updates
        if (this.getConfig().getBoolean("features.notifier")) {
            log.info("Checking for updates.");
            updater.check();
        }
        
        // Enable debugging if set in configuration
        if (this.getConfig().getBoolean("debug")) Util.debug = true; 
    }

    public void onDisable() {

    }
}
