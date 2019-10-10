package xyz.nkomarn.Harbor.util;

import org.bukkit.Bukkit;
import xyz.nkomarn.Harbor.Harbor;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Updater {

    public static String latest;

    // Checks if an update is available
    public static boolean check() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=60088");
            URLConnection request = url.openConnection();
            request.addRequestProperty("User-Agent", "Harbor");
            request.connect();

            InputStream inputStream = (InputStream) request.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            latest = reader.lines().collect(Collectors.joining(System.lineSeparator()));

            System.out.println(latest);

            if (Harbor.version.equals(latest)) return false;


            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Actually update the Harbor JAR
    public static boolean upgrade() {
        Harbor.instance.getLogger().log(Level.INFO, "Downloading Harbor version " + latest + ".");

        try {
            String jar = new File(Updater.class.getProtectionDomain().getCodeSource()
                .getLocation().getPath()).getName();

            URL url = new URL("http://aqua.api.spiget.org/v2/resources/60088/download");
            File jarFile = new File("plugins" + File.separator + jar);
            InputStream inputStream = url.openStream();

            // If Plugman is loaded, hot reload the plugin
            if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlugMan")) {
                Bukkit.getServer().broadcastMessage("Using plugman");
                Files.copy(inputStream, Paths.get(jarFile.toURI()), StandardCopyOption.REPLACE_EXISTING);

                Bukkit.getServer().broadcastMessage("Boom done and updated");
            }

            // Unload plugin and copy new JAR
            //Bukkit.getServer().getPluginManager().disablePlugin(Harbor.instance);
            //URLClassLoader classLoader = (URLClassLoader) Harbor.instance.getClass().getClassLoader();
            //classLoader.close();
            //System.gc();

            //Files.copy(inputStream, Paths.get(jarFile.toURI()), StandardCopyOption.REPLACE_EXISTING);

            // Load the new version
            //Bukkit.getServer().getPluginManager().loadPlugin(jarFile);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
