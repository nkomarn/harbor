package mykyta.Harbor;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import mykyta.Harbor.Commands.Sleeping;
import mykyta.Harbor.Events.BedEnter;
import mykyta.Harbor.Events.BedLeave;
import mykyta.Harbor.Events.Move;
import mykyta.Harbor.Events.PlayerJoin;

public class Harbor extends JavaPlugin {
    private Logger log = Bukkit.getLogger();
    private Updater updater = new Updater();

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
        if (config.getBoolean("features.notifier")) Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        util.setupNMS();

        // Enable debugging if set in configuration
        if (this.getConfig().getBoolean("debug")) Util.debug = true; 

        // Start timer task
	    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Task(), 0L, config.getInteger("values.check") * 20);

        // Check for updates
        if (this.getConfig().getBoolean("features.notifier")) {
            if (Util.debug) Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.miscellaneous.prefix")) + "Checking for new updates...");
            updater.check();
        }
        
       

        // TODO "Damage" players to kick them out of bed (reset counter)--- may not need to do cause of new system

        // FIXME Initialize HashMap items for every world
        for (World w : Bukkit.getServer().getWorlds()) {
            ArrayList<Player> sleeping = new ArrayList<Player>();
            Util.sleeping.put(w, sleeping);
        }

        //FIXME temp add players to the count on realad
        Bukkit.getServer().getWorlds().forEach(w -> {
            w.getPlayers().forEach(p -> {
                Util.activity.put(p, System.currentTimeMillis());
            });
        });
    }

    public void onDisable() {

    }
}
