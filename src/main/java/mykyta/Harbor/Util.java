package mykyta.Harbor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import mykyta.Harbor.NMS.NMS;
import mykyta.Harbor.NMS.NMS_1_10_R1;
import mykyta.Harbor.NMS.NMS_1_11_R1;
import mykyta.Harbor.NMS.NMS_1_12_R1;
import mykyta.Harbor.NMS.NMS_1_13_R1;
import mykyta.Harbor.NMS.NMS_1_13_R2;
import mykyta.Harbor.NMS.NMS_1_14_R1;
import mykyta.Harbor.NMS.NMS_1_7_R1;
import mykyta.Harbor.NMS.NMS_1_8_R1;
import mykyta.Harbor.NMS.NMS_1_9_R1;
import mykyta.Harbor.NMS.NMS_1_9_R2;

public class Util {
    public static HashMap<World, ArrayList<Player>> sleeping = new HashMap<World, ArrayList<Player>>();
    public static HashMap<Player, Long> activity = new HashMap<Player, Long>();
    public static ArrayList<Player> afk = new ArrayList<Player>();

    public String version = "1.5";
    public static boolean enabled = true;
    public static boolean debug = false;
    private static NMS nms;
    Config config = new Config();

    /**
     * Select the correct NMS classes for the server version
     */
    public void setupNMS() {            
        String version = "";
        try {version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];} 
        catch (ArrayIndexOutOfBoundsException e) { 
            Bukkit.getServer().getConsoleSender().sendMessage(config.getString("messages.miscellaneous.prefix") + "Could not get server version. The plugin may not function correctly as a result.");
            if (debug) System.err.println(e);
            Bukkit.getPluginManager().disablePlugin(Config.harbor);
            enabled = false;
        }
        if (debug) Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + config.getString("messages.miscellaneous.running").replace("[version]", version)));

        if (version.equals("v1_7_R1")) {nms = new NMS_1_7_R1();}
        else if (version.equals("v1_8_R1")) {nms = new NMS_1_8_R1();}
        //TODO more 1.8 versions
        else if (version.equals("v1_9_R1")) {nms = new NMS_1_9_R1();}
        else if (version.equals("v1_9_R2")) {nms = new NMS_1_9_R2();}
        else if (version.equals("v1_10_R1")) {nms = new NMS_1_10_R1();}
        else if (version.equals("v1_11_R1")) {nms = new NMS_1_11_R1();}
        else if (version.equals("v1_12_R1")) {nms = new NMS_1_12_R1();}
        else if (version.equals("v1_13_R1")) {nms = new NMS_1_13_R1();}
        else if (version.equals("v1_13_R2")) {nms = new NMS_1_13_R2();}
        else if (version.equals("v1_14_R1")) {nms = new NMS_1_14_R1();}
        else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + "This version of Harbor is incompatible with your server version. As such, Harbor will be disabled."));
            Bukkit.getPluginManager().disablePlugin(Config.harbor);
            enabled = false;
        }
    }

    /**
     * Increment the sleeping count for a world
     * @param world World to increment count in
     */
    public void add(World world, Player player) {
        ArrayList<Player> list = Util.sleeping.get(world);
        list.add(player);
        Util.sleeping.put(world, list);
    }

    /**
     * Decrement the sleeping count for a world
     * @param world World to increment count in
     */
    public void remove(World world, Player player) {
        ArrayList<Player> list = Util.sleeping.get(world);
        list.remove(player);
    }

    /**
     * Fetch the sleeping count for a world
     * @param world World to fetch count for
     */
    public int getSleeping(World w) {
        try {return Math.max(0, Util.sleeping.get(w).size());}
        catch (NullPointerException e) {return 0;}
    }

    /**
     * Fetch the amount of players needed to skip night
     * @param world World to fetch count for
     */
    public int getNeeded(World w) {
        //FIXME make sure to remove excluded players
        return Math.max(0, (int) Math.ceil(w.getPlayers().size() * (config.getDouble("values.percent") / 100) - this.getSleeping(w)));
    }

    /**
     * Get players in a world (returns zero if negative)
     * @param world World to check player count for
     */
    public int getOnline(World w) {
        return Math.max(0, w.getPlayers().size() - getExcluded(w).size());
    }

    /**
     * Fetch the excluded players in a world
     * @param world World to fetch count for
     */
    public ArrayList<Player> getExcluded(World w) {
        ArrayList<Player> excluded = new ArrayList<Player>();
        w.getPlayers().forEach(p -> {
            if (this.isExcluded(p)) excluded.add(p);
        });
        return excluded;
    }

    /**
     * Returns whether or not a player should be excluded from the sleep count
     * @param player Target player
     */
    public boolean isExcluded(Player p) {
        boolean state = true;
        if (config.getBoolean("features.ignore")) if (p.getGameMode() == GameMode.SURVIVAL) state = false; else state = true;
        if (config.getBoolean("features.bypass")) if (p.hasPermission("harbor.bypass")) state = true; else state = false;
        if (afk.contains(p)) state = true;
        return state;
    }

    /**
     * Sends an actionbar message to the given player
     * @param player Player to show actionbar to
     * @param message Actionbar message with color codes
     */
    public void sendActionbar(Player player, String message) {
        nms.sendActionbar(player, message);
    }

    /**
     * Sends actionbar with world information
     * @see sendActionbar(Player player, String message)
     * @param player Player to show actionbar to
     * @param message Actionbar message with color codes
     * @param world World to fetch information for
     */
    public void sendActionbar(Player p, String message, World w) {
        nms.sendActionbar(p, message
        .replace("[sleeping]", String.valueOf(this.getSleeping(w)))
        .replace("[online]", String.valueOf(this.getOnline(w)))
        .replace("[needed]", String.valueOf(this.getNeeded(w))));
    }

    /**
     * Sends a custom JSON message to selected player
     * @param player Player to send message to
     * @param JSON Message in JSON format
     */
    public void sendJSONMessage(Player player, String JSON) {
        nms.sendJSONMessage(player, JSON);
    }

    /**
     * Sends a title to selected player
     * @param player Player to send message to
     * @param JSON Message in JSON format
     */
    public void sendTitle(Player player, String top, String bottom) {
        nms.sendTitle(player, top, bottom);
    }

    /**
     * Puts selected player in a sleeping state
     * @param player Player to send message to
     * @param JSON Message in JSON format
     */
    public void enterBed(Player player) {
        nms.enterBed(player);
    }

    /**
     * Skips the night in the specified world (if possible)
     * @param World to return value for
     */
    public void skip(World w) {
        if (config.getBoolean("features.skip") && this.getNeeded(w) - this.getExcluded(w).size() == 0) {
            w.setTime(1000L);
            
            // Set weather to clear
            if (config.getBoolean("features.weather")) {
                w.setStorm(false);
                w.setThundering(false);
            }
                
            // Display messages
            if (config.getBoolean("messages.chat.chat") && (config.getString("messages.chat.skipped").length() != 0)) {
                List<String> msgs = config.getList("messages.chat.skipped");
                Random r = new Random();
                int n = r.nextInt(msgs.size());
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', msgs.get(n)));
            }
            if (config.getBoolean("features.title")) {
                w.getPlayers().forEach(p -> {
                    this.sendTitle(p, config.getString("messages.title.morning.top"), config.getString("messages.title.morning.bottom"));
                });
            }
        }
    }
}