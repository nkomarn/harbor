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

public class Updater {
    /**
     * Checks for an update using the Spiget API
     * @see https://spiget.org/
     */
    public boolean check() {
        try {
            URL url = new URL("https://api.spiget.org/v2/resources/60088/versions");
            URLConnection request = url.openConnection();
            request.connect();
        
            Util util = new Util();
            ArrayList<String> releases = new ArrayList<String>();
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(new InputStreamReader((InputStream) request.getContent())); 
            JsonArray versions = element.getAsJsonArray();
            
            for (JsonElement version : versions) {
                JsonObject id = version.getAsJsonObject();
                releases.add(id.get("name").getAsString());
            }

            // TODO add super secret message for when the version is newer than the latest published one
            if (util.version.equals(releases.get(releases.size() - 1))) {
                System.out.println("Running latest version.");
                return false;
            }
            else {
                System.out.println("Running an outdated version! Latest is " + releases.get(releases.size() - 1));
                return true;
            }
            

            /*
            int current = Integer.parseInt(String.valueOf(util.version).replace(".", ""));
            int latest = releases.get(releases.size() - 1);
            System.out.println("Current version: " + current);
            System.out.println("Latest release: "  + latest);
            if (util.version < latest) {
                System.out.println("Update available! Version " + String.valueOf(latest));
                return true;    
            }
            else if (util.version < latest) {
                System.out.println("Hmm... you're using some sort of time travel technology (your version is newer than the latest release). Welp, at least you don't have updates to worry about any time soon.");
                return true;   
            }
            else {
                System.out.println("Using the latest version.");
                return false; 
            }  */
        }
        catch (IOException e) {
            System.out.println("Failed to check for updates.");
            return false;
        }
    }
}