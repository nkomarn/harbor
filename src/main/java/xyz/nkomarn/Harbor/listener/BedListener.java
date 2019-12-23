package xyz.nkomarn.Harbor.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Messages;

public class BedListener implements Listener {
    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
        Messages.sendWorldChatMessage(event.getBed().getWorld(),
                Config.getString("messages.chat.sleeping")
                        .replace("[player]", event.getPlayer().getName())
                        .replace("[displayname]", event.getPlayer().getDisplayName()));
    }

    @EventHandler
    public void onBedLeave(final PlayerBedEnterEvent event) {
        Messages.sendWorldChatMessage(event.getBed().getWorld(),
                Config.getString("messages.chat.left")
                        .replace("[player]", event.getPlayer().getName())
                        .replace("[displayname]", event.getPlayer().getDisplayName()));
    }
}
