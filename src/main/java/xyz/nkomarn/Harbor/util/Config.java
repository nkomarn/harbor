package xyz.nkomarn.Harbor.util;

import xyz.nkomarn.Harbor.Harbor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Config {
    /**
     * Report an error in reading the configuration
     * @param e Exception generated from reading configuration
     */
    private static void error(Exception e) {
        e.printStackTrace();
    }

    /**
     * Fetches a boolean from the configuration
     * @param location Configuration location of the boolean
     */
    public static boolean getBoolean(String location) {
        try {
            return Harbor.instance.getConfig().getBoolean(location);
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
    public static String getString(String location) {
        try {
            return Harbor.instance.getConfig().getString(location);
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
    public static int getInteger(String location) {
        try {
            return Harbor.instance.getConfig().getInt(location);
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
    public static double getDouble(String location) {
        try {
            return Double.parseDouble(Objects.requireNonNull(Harbor.instance.getConfig().getString(location)));
        }
        catch (Exception e) {
            error(e);
            return 0.0;
        }
    }

    /**
     * Fetches a double from the configuration
     * @param location Configuration location of the double
     */
    public static List<String> getList(String location) {
        try {
            return Harbor.instance.getConfig().getStringList(location);
        }
        catch (Exception e) {
            error(e);
            return new ArrayList<>();
        }
    }
}
