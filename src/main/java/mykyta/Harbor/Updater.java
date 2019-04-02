package mykyta.Harbor;

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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class Updater {
    private String latest = "";
    
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
            request.connect();
        
            ArrayList<String> releases = new ArrayList<String>();
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(new InputStreamReader((InputStream) request.getContent())); 
            JsonArray versions = element.getAsJsonArray();
            
            for (JsonElement version : versions) {
                JsonObject id = version.getAsJsonObject();
                releases.add(id.get("name").getAsString());
            }

            if (util.version.equals(releases.get(releases.size() - 1))) {
                if (Util.debug) c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "Currently running the latest version of Harbor.");
                return false;
            }
            else if (!releases.contains(util.version)) {
                if (config.getBoolean("features.notifier")) c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "You have a version of Harbor that is newer than the latest available version. Great work!");
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
     * Returns the latest version number
     */
    public String getLatest() {
        return latest;
    }
}