package mykyta.Harbor;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.entity.Player;

import mykyta.Harbor.Actionbar.Actionbar;

public class Util implements Actionbar {
    public static HashMap<World, Integer> sleeping = new HashMap<World, Integer>();
    public String version = "1.5";
    private Actionbar actionbar;

    Harbor harbor;
    public Util(Harbor instance) {
        harbor = instance;
    }

    /**
     * Sends an actionbar message to the given player
     * @param player Player to show actionbar to
     * @param message Actionbar message with color codes
     */
    public void sendActionbar(Player player, String message) {
        actionbar.sendActionbar(player, message);
    }

    /**
     * Sends actionbar with world information
     * @see sendActionbar(Player player, String message)
     * @param player Player to show actionbar to
     * @param message Actionbar message with color codes
     * @param world World to fetch information for
     */
    public void sendActionbar(Player player, String message, World world) {
        actionbar.sendActionbar(player, message
        .replace("[sleeping]", String.valueOf(sleeping.get(world)))
        //TODO add bypassers functionaliyt .replace("[online]", String.valueOf(world.getPlayers().size() - bypassers.size()))
        //  .replace("[needed]", String.valueOf(Math.max(0, Math.round(world.getPlayers().size() * Float.parseFloat(plugin.getConfig().getString("values.percent")) - bypassers.size() - ((Integer)worlds.get(world)).intValue())))));
        .replace("[online]", String.valueOf(world.getPlayers().size()))
        .replace("[needed]", String.valueOf(Math.max(0, Math.round(world.getPlayers().size() * Float.parseFloat(harbor.getConfig().getString("values.percent")) - (sleeping.get(world)).intValue())))));
    }
}