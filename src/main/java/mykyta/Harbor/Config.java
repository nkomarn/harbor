package mykyta.Harbor;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class Config {
    private Logger log = Bukkit.getLogger();
    private String error = "An error occured while trying to read the configuration. The plugin may not function correctly as a result.";
    private static Harbor harbor;

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
        log.severe(error);
        if (Util.debug) System.err.println(e);
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
}