package mykyta.Harbor;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import mykyta.Harbor.Events.BedEnter;

public class Harbor extends JavaPlugin {
    private Logger log = Bukkit.getLogger();
    private Updater updater = new Updater(this);

    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new BedEnter(this), this);

        // Check for updates
        if (this.getConfig().getBoolean("features.notifier")) {
            System.out.println("Checking for updates.");
            updater.check();
        }
    }

    public void onDisable() {

    }
}
