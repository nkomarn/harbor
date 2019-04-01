package mykyta.Harbor;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
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
     * Fetch the included players in a world
     * @param world World to fetch count for
     */
    public ArrayList<Player> getIncluded(World world) {
        //TODO convert to a foreach
        ArrayList<Player> players = new ArrayList<Player>();
        for (Player player : world.getPlayers()) {
            if (this.isIncluded(player)) players.add(player);
        }
        return players;

        // FIXME 2 fancy 4 me to understand
        /*world.getPlayers().stream().filter(p -> this.isIncluded(world.getPlayers)).forEach(p -> {
            if (true) {
                players.add(p);
            }
        });*/
    }


    /**
     * Returns true if player should be included in count
     * @param player Target player
     */
    public boolean isIncluded(Player player) {
        if (config.getBoolean("features.ignore")) {
            if (player.getGameMode() == GameMode.SURVIVAL) return true;
            else return false;
        }
        else if (config.getBoolean("features.bypass")) {
            if (player.hasPermission("harbor.bypass")) return false;
            else return true;
        }
        else if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - activity.get(player)) > config.getInteger("values.timeout")) {
            return false;
        }
        else return true;
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
    public void sendActionbar(Player player, String message, World world) {
        Config config = new Config();
        nms.sendActionbar(player, message
        .replace("[sleeping]", String.valueOf(sleeping.get(world)))
        //TODO add bypassers functionaliyt .replace("[online]", String.valueOf(world.getPlayers().size() - bypassers.size()))
        //  .replace("[needed]", String.valueOf(Math.max(0, Math.round(world.getPlayers().size() * Float.parseFloat(plugin.getConfig().getString("values.percent")) - bypassers.size() - ((Integer)worlds.get(world)).intValue())))));
        .replace("[online]", String.valueOf(world.getPlayers().size()))
        .replace("[needed]", String.valueOf(Math.max(0, Math.round(world.getPlayers().size() * Float.parseFloat(config.getString("values.percent")) - sleeping.get(world).size())))));
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
}