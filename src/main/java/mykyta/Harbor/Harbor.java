package mykyta.Harbor;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import mykyta.Harbor.Commands.Command;
import mykyta.Harbor.Commands.Sleeping;
import mykyta.Harbor.Events.BedEnter;
import mykyta.Harbor.Events.BedLeave;
import mykyta.Harbor.Events.GUIClick;
import mykyta.Harbor.Events.GUIDrag;
import mykyta.Harbor.Events.Move;
import mykyta.Harbor.Events.PlayerJoin;
import mykyta.Harbor.Events.PlayerLeave;
import mykyta.Harbor.Events.Spawn;

public class Harbor extends JavaPlugin {
    public void onEnable() {
        Config config = new Config();
        Util util = new Util();
        config.setInstance(this);
        saveDefaultConfig();
        this.getCommand("harbor").setExecutor(new Command());
        this.getCommand("sleeping").setExecutor(new Sleeping());
        Bukkit.getPluginManager().registerEvents(new BedEnter(), this);
        Bukkit.getPluginManager().registerEvents(new BedLeave(), this);
        Bukkit.getPluginManager().registerEvents(new Move(), this);
        Bukkit.getPluginManager().registerEvents(new Spawn(), this);
        Bukkit.getPluginManager().registerEvents(new GUIClick(), this);
        Bukkit.getPluginManager().registerEvents(new GUIDrag(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerLeave(), this);
        util.setupNMS();
	    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Task(), 0L, config.getInteger("values.clock") * 20);
        if (this.getConfig().getBoolean("debug")) Util.debug = true; 
        if (this.getConfig().getBoolean("features.notifier")) {
            if (Util.debug) Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.miscellaneous.prefix")) + "Checking for new updates...");
            Updater updater = new Updater();
            updater.check();
        }
        for (World w : Bukkit.getServer().getWorlds()) {
            ArrayList<Player> sleeping = new ArrayList<Player>();
            Util.sleeping.put(w, sleeping);
        }
        Bukkit.getServer().getWorlds().forEach(w -> {
            w.getPlayers().forEach(p -> {
                Util.activity.put(p, System.currentTimeMillis());
            });
        });
    }

    public void onDisable() {

    }
}
