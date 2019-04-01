package mykyta.Harbor.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import mykyta.Harbor.EncodingUtils;
import mykyta.Harbor.GUI;
import mykyta.Harbor.Util;
import mykyta.Harbor.GUI.GUIType;

public class GUIClick implements Listener {
    @EventHandler
    public void onGUIClick(InventoryClickEvent event) {
        String title = event.getInventory().getName();
        if (EncodingUtils.hasHiddenString(title)) {
            // Extract hidden data
            String raw = EncodingUtils.extractHiddenString(title);
            JSONParser parser = new JSONParser();
            JSONObject json = new JSONObject();
            try {json = (JSONObject) parser.parse(raw);}
            catch (Exception e) {if (Util.debug) e.printStackTrace();}
            String type = json.get("GUIType").toString();

            System.out.println("type");
            if (type.equals("SLEEPING")) {
                event.setCancelled(true);
                return;
            }
        } 
        else System.out.println("asdfasd");;
    }
}