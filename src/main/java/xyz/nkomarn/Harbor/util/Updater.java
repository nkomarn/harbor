package xyz.nkomarn.Harbor.util;

import xyz.nkomarn.Harbor.Harbor;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Updater {
    public static String latest;

    // Checks if an update is available
    public static Future<Boolean> check() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        ForkJoinPool.commonPool().submit(() -> {
            try {
                URL latestVersion = new URL("https://api.spigotmc.org/legacy/update.php?resource=60088");
                URLConnection request = latestVersion.openConnection();
                request.addRequestProperty("User-Agent", "Harbor");
                request.connect();
                InputStream inputStream = request.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                latest = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
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

        ForkJoinPool.commonPool().submit(() -> {
            String jarName = new File(Updater.class.getProtectionDomain().getCodeSource().getLocation()
                .getPath()).getName();

                try {
                    URL downloadURL = new URL("http://aqua.api.spiget.org/v2/resources/60088/download");
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
}
