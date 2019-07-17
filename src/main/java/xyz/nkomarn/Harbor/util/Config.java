package xyz.nkomarn.Harbor.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import xyz.nkomarn.Harbor.Harbor;

public class Config {
    private ConsoleCommandSender c = Bukkit.getServer().getConsoleSender();

    /**
     * Report an error in reading the configuration
     * @param e Exception generated from reading configuration
     */
    private void error(Exception e) {
        c.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "An error occurred while trying to read the configuration. Harbor may not function correctly as a result."));
        if (Harbor.debug) e.printStackTrace();
    }

    /**
     * Fetches a boolean from the configuration
     * @param location Configuration location of the boolean
     */
    public boolean getBoolean(String location) {
        try {return Harbor.instance.getConfig().getBoolean(location);}
        catch (Exception e) {error(e); return false;}
    }

    /**
     * Fetches a string from the configuration
     * @param location Configuration location of the string
     */
    public String getString(String location) {
        try {return Harbor.instance.getConfig().getString(location);}
        catch (Exception e) {error(e); return "";}
    }

    /**
     * Fetches an integer from the configuration
     * @param location Configuration location of the integer
     */
    public int getInteger(String location) {
        try {return Harbor.instance.getConfig().getInt(location);}
        catch (Exception e) {error(e); return 0;}
    }

    /**
     * Fetches a double from the configuration
     * @param location Configuration location of the double
     */
    public double getDouble(String location) {
        try {return Double.parseDouble(Harbor.instance.getConfig().getString(location));}
        catch (Exception e) {error(e); return 0.0;}
    }

    /**
     * Fetches a double from the configuration
     * @param location Configuration location of the double
     */
    public List<String> getList(String location) {
        try {return Harbor.instance.getConfig().getStringList(location);}
        catch (Exception e) {error(e); return new ArrayList<String>();}
    }
}