package mykyta.Harbor;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import mykyta.Harbor.NMS.NMS;
import mykyta.Harbor.NMS.NMS_1_13_R2;
import net.md_5.bungee.api.ChatColor;

public class Util {
    public static HashMap<World, Integer> sleeping = new HashMap<World, Integer>();
    public String version = "1.4.2";
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
    public void increment(World world) {
        int count;
        try {count = Util.sleeping.get(world);}
        catch (Exception e){count = 0;}
        Util.sleeping.put(world, count + 1);
    }

    /**
     * Decrement the sleeping count for a world
     * @param world World to increment count in
     */
    public void decrement(World world) {
        int count;
        try {count = Util.sleeping.get(world);}
        catch (Exception e){count = 0;}
        Util.sleeping.put(world, count - 1);
    }

    /**
     * Fetch the sleeping count for a world
     * @param world World to fetch count for
     */
    public int fetch(World world) {
        int count;
        try {count = Util.sleeping.get(world);}
        catch (Exception e){count = 0;}
        return count;
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
        .replace("[needed]", String.valueOf(Math.max(0, Math.round(world.getPlayers().size() * Float.parseFloat(config.getString("values.percent")) - (sleeping.get(world)).intValue())))));
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
     * Returns true if player is in survival mode
     * @param player Target player
     */
    public boolean isSurvival(Player player) {
        if (player.getGameMode() == GameMode.SURVIVAL) return true;
        else return false;
    }
}