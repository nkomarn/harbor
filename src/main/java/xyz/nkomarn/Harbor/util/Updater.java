package xyz.nkomarn.Harbor.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import xyz.nkomarn.Harbor.Harbor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

public class Updater {
    public static String latest = "";

    /**
     * Checks for an update using the Spigot API
     */
    public boolean check() {
        ConsoleCommandSender c = Bukkit.getServer().getConsoleSender();
        Config config = new Config();

        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=60088");
            URLConnection request = url.openConnection();
            request.addRequestProperty("User-Agent", "Harbor");
            request.connect();

            StringWriter writer = new StringWriter();
            IOUtils.copy((InputStream) request.getContent(), writer, "UTF-8");
            latest = writer.toString();

            if (Harbor.version.equals(latest)) {
                if (Harbor.debug) c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "Currently running the latest version of Harbor.");
                return false;
            }
            else {
                if (config.getBoolean("features.notifier")) c.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        config.getString("messages.miscellaneous.prefix")) + "Currently running an outdated version of Harbor. The latest available release is version " + latest + ".");
                return true;
            }
        }
        catch (IOException e) {
            if (Harbor.debug) c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "Failed to check for updated releases of Harbor.");
            return false;
        }
    }

    /**
     * Replace the current Harbor version with the latest available release
     */
    public int upgrade() {
        ConsoleCommandSender c = Bukkit.getServer().getConsoleSender();
        Config config = new Config();

        if (Harbor.version.equals(latest)) return 2;

        c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "Downloading Harbor version " + latest + ".");
        try {
            String jar = new File(Updater.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
            new File("update").mkdir();
            URL url = new URL("http://aqua.api.spiget.org/v2/resources/60088/download");
            File f = new File("plugins" + File.separator + "update" + File.separator + jar);
            FileUtils.copyURLToFile(url, f);
            c.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.miscellaneous.prefix")) + "Harbor " + latest + " has been downloaded successfully and will be enabled after a server restart/reload.");
            return 0;
        }
        catch (Exception e) {
            c.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.prefix")) + "Failed to update Harbor to version " + latest + ".");
            if (Harbor.debug) e.printStackTrace();
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
