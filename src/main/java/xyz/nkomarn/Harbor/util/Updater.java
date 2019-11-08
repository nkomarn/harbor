package xyz.nkomarn.Harbor.util;

import org.bukkit.Bukkit;
import xyz.nkomarn.Harbor.Harbor;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Updater {

    public static String latest;

    // Checks if an update is available
    public static Future<Boolean> check() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try {
                URL latestVersion = new URL("https://api.spigotmc.org/legacy/update.php?resource=60088");
                URLConnection request = latestVersion.openConnection();
                request.addRequestProperty("User-Agent",  "Harbor");
                request.connect();
                InputStream inputStream = request.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                latest = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
                System.out.println(latest); // TODO REMOVE
                future.complete(!Harbor.version.equals(latest));
            } catch (IOException e) {
                future.complete(false);
                e.printStackTrace();
            }
        });

        return future;
    }

    // Download latest JAR and put it in Bukkit's update folder
    public static Future<String> upgrade() {
        CompletableFuture<String> future = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            String jarName = new File(Updater.class.getProtectionDomain().getCodeSource().getLocation()
                .getPath()).getName();

                try {
                    URL downloadURL = new URL("http://aqua.api.spiget.org/v2/resources/60088/download");
                    // TODO File jarFile = new File("plugins" + File.separator + jarName);
                    File updatedJarFile = new File("plugins" + File.separator + "update"
                            + File.separator + jarName);
                    updatedJarFile.mkdirs();
                    InputStream inputStream = downloadURL.openStream();
                    Files.copy(inputStream, Paths.get(updatedJarFile.toURI()), StandardCopyOption.REPLACE_EXISTING);
                    future.complete("Updated Harbor. Changes will take effect after a server reload/reboot.");
                } catch (IOException e) {
                    future.complete("Failed to update Harbor. Check console for full log.");
                    e.printStackTrace();
                }
        });
        return future;
    }

    // Actually update the Harbor JAR
    /*public static boolean upgrade() {
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
    }*/

}
