package mykyta.Harbor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class Config {
    private String error = "An error occured while trying to read the configuration. Harbor may not function correctly as a result.";
    public static Harbor harbor;
    private ConsoleCommandSender c = Bukkit.getServer().getConsoleSender();

    /**
     * Sets main class instance for accessing configuration
     * @param instance Instance of the main class
     */
    public void setInstance(Harbor instance) {
        harbor = instance;
    }

    /**
     * Report an error in reading the configuration
     * @param e Exception generated from reading configuration
     */
    private void error(Exception e) {
        c.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getString("messages.miscellaneous.prefix") + error));
        if (Util.debug) e.printStackTrace();
    }

    /**
     * Fetches a boolean from the configuration
     * @param location Configuration location of the boolean
     */
    public boolean getBoolean(String location) {
        try {
            return harbor.getConfig().getBoolean(location);
        }
        catch (Exception e) {
            error(e);
            return false;
        }
    }

    /**
     * Fetches a string from the configuration
     * @param location Configuration location of the string
     */
    public String getString(String location) {
        try {
            return harbor.getConfig().getString(location);
        }
        catch (Exception e) {
            error(e);
            return "";
        }
    }

    /**
     * Fetches an integer from the configuration
     * @param location Configuration location of the integer
     */
    public int getInteger(String location) {
        try {
            return harbor.getConfig().getInt(location);
        }
        catch (Exception e) {
            error(e);
            return 0;
        }
    }

    /**
     * Fetches a double from the configuration
     * @param location Configuration location of the double
     */
    public double getDouble(String location) {
        try {
            return Double.parseDouble(harbor.getConfig().getString(location));
        }
        catch (Exception e) {
            error(e);
            return 0.0;
        }
    }
}