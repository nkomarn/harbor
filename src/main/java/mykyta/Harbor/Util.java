package mykyta.Harbor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import mykyta.Harbor.NMS.NMS;
import mykyta.Harbor.NMS.NMS_1_13_R2;
import net.md_5.bungee.api.ChatColor;

public class Util {
    public static HashMap<World, ArrayList<Player>> sleeping = new HashMap<World, ArrayList<Player>>();
    public static HashMap<Player, Long> activity = new HashMap<Player, Long>();
    public static ArrayList<Player> afk = new ArrayList<Player>();
    public String version = "1.5";
    public static boolean debug = false;
    private Logger log = Bukkit.getLogger();
    private static NMS nms;
    Config config = new Config();

    /**
     * Select the correct NMS classes for the server version
     */
    public void setupNMS() {            
        String version = "";
        try {version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];} 
        catch (ArrayIndexOutOfBoundsException e) { 
            log.severe("Could not get server version. The plugin may not function correctly as a result.");
            if (Util.debug) System.err.println(e);
        }
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix") + config.getString("messages.miscellaneous.running").replace("[version]", version)));
            
        if (version.equals("v1_13_R2")) {
            nms = new NMS_1_13_R2();
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
        Util.sleeping.put(world, list);
    }

    /**
     * Fetch the sleeping count for a world
     * @param world World to fetch count for
     */
    public int getSleeping(World world) {
        return Util.sleeping.get(world).size();
    }

    /**
     * Fetch the amount of players needed to skip night
     * @param world World to fetch count for
     */
    public int getNeeded(World world) {
        return Math.max(0, (int) Math.ceil(world.getPlayers().size() * (config.getDouble("values.percent") / 100) - this.getSleeping(world)));
    }

    /**
     * Fetch the amount of players needed to skip night (minus one)
     * @param world World to fetch count for
     */
    public int getNeededDecremented(World world) {
        return Math.max(0, (int) Math.ceil((world.getPlayers().size() - 1) * (config.getDouble("values.percent") / 100) - this.getSleeping(world)));
    }
    
    /**
     * Fetch the included players in a world
     * @param world World to fetch count for
     */
    public ArrayList<Player> getIncluded(World world) {
        ArrayList<Player> players = new ArrayList<Player>();
        world.getPlayers().forEach(p -> {
            if (this.isIncluded(p)) players.add(p);
        });
        return players;
    }


    /**
     * Returns true if player should be included in count
     * @param player Target player
     */
    public boolean isIncluded(Player p) {
        boolean state = true;
        if (config.getBoolean("features.ignore")) if (p.getGameMode() == GameMode.SURVIVAL) state = true; else state = false;
        if (config.getBoolean("features.bypass")) if (p.hasPermission("harbor.bypass")) state = false; else state = true;
        if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - activity.get(p)) > config.getInteger("values.timeout")) state = false;
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
        ArrayList<Player> included = this.getIncluded(w);
        int excluded = w.getPlayers().size() - included.size();

        nms.sendActionbar(p, message
        .replace("[sleeping]", String.valueOf(this.getSleeping(w)))
        .replace("[online]", String.valueOf(included.size()))
        .replace("[needed]", String.valueOf(this.getNeeded(w) - excluded)));
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
     * Skips the night in the specified world (if possible)
     * @param World to return value for
     */
    public void skip(World w, int excluded, int needed) {
        if (config.getBoolean("features.skip") && (needed - excluded) == 0) {
            System.out.println("set time");
            w.setTime(1000L);
            
            // Set weather to clear
            if (config.getBoolean("features.weather")) {
                w.setStorm(false);
                w.setThundering(false);
            }
                
            // Display messages
            if (config.getBoolean("messages.chat.chat") && (config.getString("messages.chat.skipped").length() != 0)) Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.chat.skipped")));
            if (config.getBoolean("features.title")) {
                System.out.println("sent message");
                w.getPlayers().forEach(p -> {
                    this.sendTitle(p, config.getString("messages.title.morning.top"), config.getString("messages.title.morning.bottom"));
                });
            }
        }
    }
}