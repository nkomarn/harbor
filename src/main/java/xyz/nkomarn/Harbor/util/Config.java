package xyz.nkomarn.Harbor.util;

import xyz.nkomarn.Harbor.Harbor;

import java.util.ArrayList;
import java.util.List;

public class Config {
    /**
     * Fetches a boolean from the configuration
     * if location is not found, false is returned
     * @param location Configuration location of the boolean
     */
    public static boolean getBoolean(final String location) {
        return Harbor.getHarbor().getConfig().getBoolean(location, false);
    }

    /**
     * Fetches a string from the configuration
     * if location is not found, empty string is returned
     * @param location Configuration location of the string
     */
    public static String getString(final String location) {
        return Harbor.getHarbor().getConfig().getString(location, "");
    }

    /**
     * Fetches an integer from the configuration
     * if location is not found, 0 is returned
     * @param location Configuration location of the integer
     */
    public static int getInteger(final String location) {
        return Harbor.getHarbor().getConfig().getInt(location, 0);
    }

    /**
     * Fetches a double from the configuration
     * if location is not found, 0.0 is returned
     * @param location Configuration location of the double
     */
    public static double getDouble(final String location) {
        return Harbor.getHarbor().getConfig().getDouble(location, 0.0);
    }

    /**
     * Fetches a list from the configuration
     * if location is not found, empty list is returned
     * @param location Configuration location of the list
     */
    public static List<String> getList(final String location) {
        return (List<String>) Harbor.getHarbor().getConfig().getList(location, new ArrayList<>());
    }
}
