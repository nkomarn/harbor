package techtoolbox.Harbor;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import techtoolbox.Harbor.Actionbar.Actionbar;
import techtoolbox.Harbor.Actionbar.Actionbar_1_10_R1;
import techtoolbox.Harbor.Actionbar.Actionbar_1_11_R1;
import techtoolbox.Harbor.Actionbar.Actionbar_1_12_R1;
import techtoolbox.Harbor.Actionbar.Actionbar_1_13_R1;
import techtoolbox.Harbor.Actionbar.Actionbar_1_13_R2;
import techtoolbox.Harbor.Actionbar.Actionbar_1_8_R1;
import techtoolbox.Harbor.Actionbar.Actionbar_1_8_R2;
import techtoolbox.Harbor.Actionbar.Actionbar_1_8_R3;
import techtoolbox.Harbor.Actionbar.Actionbar_1_9_R1;
import techtoolbox.Harbor.Listeners.BedEnterEvent;
import techtoolbox.Harbor.Listeners.BedLeaveEvent;
import techtoolbox.Harbor.Listeners.JoinEvent;
import techtoolbox.Harbor.Listeners.QuitEvent;

public class Main extends JavaPlugin implements Listener {
	public static Main plugin;
	public static String version = "1.4.4";
	public static HashMap<World, Integer> worlds = new HashMap<World, Integer>();
	public static ArrayList<String> bypassers = new ArrayList<String>();
	public static Actionbar actionbar;
	
	public void onEnable() {
	    plugin = this;
	    saveDefaultConfig();
	    getCommand("harbor").setExecutor(new Command());
	    Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
	    Bukkit.getPluginManager().registerEvents(new QuitEvent(), this);
	    Bukkit.getPluginManager().registerEvents(new BedEnterEvent(), this);
	    Bukkit.getPluginManager().registerEvents(new BedLeaveEvent(), this);
	    
	    // Fill up worlds list
	    if (getConfig().getBoolean("features.bypass")) {
		    for (int i = 0; Bukkit.getServer().getWorlds().size() > i; i++) {
		    	worlds.put(Bukkit.getServer().getWorlds().get(i), Integer.valueOf(0));
		    }
	    }

	    // Start actionbar message check
	    if (getConfig().getBoolean("messages.actionbar.actionbar")) {
	    	Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ActionBarMessage(), 0L, getConfig().getInt("values.check") * 20);
	    }

	    // Set up actionbar NMS
	    if (setupActionbar()) {
	    	Bukkit.getPluginManager().registerEvents(this, this);
	    } else {
	    	String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	    	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.miscellaneous.incompatible").replace("[version]", version)));
	    	Bukkit.getPluginManager().disablePlugin(this);
	    }
	}
	
	public void onDisable() {
		// Knock players out of bed
	    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
	    	player.damage(0.000001);
	    }
	}
	
	public static void sendActionbar(String type, World world, Player player) {
		if (type.equals("playersInBed")) {
			for (Player p : world.getPlayers()) {
				actionbar.sendActionbar(p, plugin.getConfig().getString("messages.actionbar.sleeping")
		          .replace("[sleeping]", String.valueOf(worlds.get(world)))
		          .replace("[online]", String.valueOf(world.getPlayers().size() - bypassers.size()))
		          .replace("[needed]", String.valueOf(Math.max(0, Math.round(world.getPlayers().size() * Float.parseFloat(plugin.getConfig().getString("values.percent")) - bypassers.size() - ((Integer)worlds.get(world)).intValue())))));
			} 
		} 
		else if (type.equals("everyoneSleeping")) {
			for (Player p : world.getPlayers()) {
				actionbar.sendActionbar(p, plugin.getConfig().getString("messages.actionbar.everyone")
		          .replace("[sleeping]", String.valueOf(worlds.get(world)))
		          .replace("[online]", String.valueOf(world.getPlayers().size() - bypassers.size()))
		          .replace("[needed]", String.valueOf(Math.max(0, Math.round(world.getPlayers().size() * Float.parseFloat(plugin.getConfig().getString("values.percent")) - bypassers.size() - ((Integer)worlds.get(world)).intValue())))));
			}
		}
		else if (type.equals("sleepingBlocked")) {
			actionbar.sendActionbar(player, plugin.getConfig().getString("messages.actionbar.blocked"));
		}
	}
	
	private boolean setupActionbar() {
		String version;
		  
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		} 
		catch (ArrayIndexOutOfBoundsException e) { 
			if (plugin.getConfig().getBoolean("features.debug")) {
				e.printStackTrace();
			}
			return false;
		}
	    
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.miscellaneous.prefix") + plugin.getConfig().getString("messages.miscellaneous.running").replace("[version]", version)));
		
		// Actionbar
		if (version.equals("v1_8_R1")) {
			actionbar = new Actionbar_1_8_R1();
		}else if (version.equals("v1_8_R2")) {
			actionbar = new Actionbar_1_8_R2();
		}else if (version.equals("v1_8_R3")) {
			actionbar = new Actionbar_1_8_R3();
		}else if (version.equals("v1_9_R1")) {
			actionbar = new Actionbar_1_9_R1();
		}else if (version.equals("v1_10_R1")) {
			actionbar = new Actionbar_1_10_R1();
		}else if (version.equals("v1_11_R1")) {
			actionbar = new Actionbar_1_11_R1();
		}else if (version.equals("v1_12_R1")) {
			actionbar = new Actionbar_1_12_R1();
		}else if (version.equals("v1_13_R1")) {
			actionbar = new Actionbar_1_13_R1();
		} else if (version.equals("v1_13_R2")) {
			actionbar = new Actionbar_1_13_R2();
		}
		return actionbar != null;
	}
}
