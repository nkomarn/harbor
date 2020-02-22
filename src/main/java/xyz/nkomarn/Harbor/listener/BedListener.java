package xyz.nkomarn.Harbor.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.task.Checker;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Messages;

public class BedListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBedEnter(final PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
        if (Checker.skippingWorlds.contains(event.getPlayer().getWorld())) return;
        Bukkit.getScheduler().runTaskLater(Harbor.instance, () -> Messages.sendWorldChatMessage(event.getBed().getWorld(),
                Config.getString("messages.chat.sleeping")
                        .replace("[player]", event.getPlayer().getName())
                        .replace("[displayname]", event.getPlayer().getDisplayName())), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBedLeave(final PlayerBedLeaveEvent event) {
        if (Checker.skippingWorlds.contains(event.getPlayer().getWorld())) return;
        Messages.sendWorldChatMessage(event.getBed().getWorld(),
                Config.getString("messages.chat.left")
                        .replace("[player]", event.getPlayer().getName())
                        .replace("[displayname]", event.getPlayer().getDisplayName()));
    }
}
