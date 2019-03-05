package mykyta.Harbor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import mykyta.Harbor.Events.BedEnter;

public class Harbor extends JavaPlugin {
    Updater updater = new Updater();

    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new BedEnter(this), this);
        System.out.println("Checking for updates");
        updater.check();
    }

    public void onDisable() {

    }
}
