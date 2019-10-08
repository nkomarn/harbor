package xyz.nkomarn.Harbor.util;

import xyz.nkomarn.Harbor.Harbor;

import java.util.ArrayList;
import java.util.List;

public class Config {
    /**
     * Fetches a boolean from the configuration
     * if location is not found, <code>false</code> is returned
     *
     * @param location Configuration location of the boolean
     */
    public static boolean getBoolean(final String location) {
        return Harbor.instance.getConfig().getBoolean(location, false);
    }

    /**
     * Fetches a string from the configuration
     * if location is not found, <code>empty String</code> is returned
     *
     * @param location Configuration location of the string
     */
    public static String getString(final String location) {
        return Harbor.instance.getConfig().getString(location, "");
    }

    /**
     * Fetches an integer from the configuration
     * if location is not found, <code>0</code> is returned
     *
     * @param location Configuration location of the integer
     */
    public static int getInteger(final String location) {
        return Harbor.instance.getConfig().getInt(location, 0);
    }

    /**
     * Fetches a double from the configuration
     * if location is not found, <code>0.0</code> is returned
     *
     * @param location Configuration location of the double
     */
    public static double getDouble(final String location) {
        return Harbor.instance.getConfig().getDouble(location, 0.0);
    }

    /**
     * Fetches a list from the configuration
     * if location is not found, <code>empty list</code> is returned
     *
     * @param location Configuration location of the list
     */
    public static List<String> getList(final String location) {
        return (List<String>) Harbor.instance.getConfig().getList(location, new ArrayList<>());
    }
}
