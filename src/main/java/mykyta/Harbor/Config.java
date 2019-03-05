package mykyta.Harbor;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class Config {
    private Logger log = Bukkit.getLogger();
    private String error = "An error occured while trying to read the configuration. The plugin may not function correctly as a result.";

    /**
     * Gets a boolean from the configuration
     * @param location Config location of the boolean
     */
    public boolean getBool(String location) {
        try {

        }
        catch (Exception e) {
            log.severe(error);
            if (Util.debug) System.err.println(e);
            return false;
        }
    }
}