package mykyta.Harbor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class Updater {
    public static String latest = "";
    public static ArrayList<String> releases = new ArrayList<String>();
    
    /**
     * Checks for an update using the Spiget API
     * @see https://spiget.org/
     */
    public boolean check() {
        ConsoleCommandSender c = Bukkit.getServer().getConsoleSender();
        Util util = new Util();
        Config config = new Config();

        try {
            URL url = new URL("https://api.spiget.org/v2/resources/60088/versions");
            URLConnection request = url.openConnection();
            request.addRequestProperty("User-Agent", "Harbor");
            request.connect();

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(new InputStreamReader((InputStream) request.getContent())); 
            JsonArray versions = element.getAsJsonArray();
            
            versions.forEach(v -> {
                JsonObject n = v.getAsJsonObject();
                releases.add(n.get("name").getAsString());
            });

            latest = releases.get(releases.size() - 1); 

            if (util.version.equals(latest)) {
                if (Util.debug) c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "Currently running the latest version of Harbor.");
                return false;
            }
            else if (!releases.contains(util.version)) {
                if (config.getBoolean("features.notifier")) c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "Running an unreleased version of Harbor.");
                return false;
            }
            else {
                latest = releases.get(releases.size() - 1); 
                if (config.getBoolean("features.notifier")) c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "Currently running an outdated version of Harbor. The latest available release is version " + latest + ".");
                return true;
            }
        }
        catch (IOException e) {
            if (Util.debug) c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "Failed to check for updated releases of Harbor.");
            return false;
        }
    }

    /**
     * Replace the current Harbor version with the latest available release
     */
    public int upgrade() {
        ConsoleCommandSender c = Bukkit.getServer().getConsoleSender();
        Config config = new Config();
        Util util = new Util();

        if (util.version.equals(latest) || !releases.contains(util.version)) return 2;

        c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "Downloading Harbor version " + latest + ".");
        try {
            String jar = new File(Updater.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
            new File("update").mkdir();
            URL url = new URL("http://aqua.api.spiget.org/v2/resources/60088/download");
            File f = new File("plugins" + File.separator + "update" + File.separator + jar);
            FileUtils.copyURLToFile(url, f);
            c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "Harbor " + latest + " has been downloaded successfully and will be enabled after a server restart/reload.");
            return 0;
        }
        catch (Exception e) {
            c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "Failed to update Harbor to version " + latest + ".");
            if (Util.debug) e.printStackTrace();
            return 1;
        }
    }

    /**
     * Returns the latest version number
     */
    public String getLatest() {
        return latest;
    }
}