package xyz.nkomarn.Harbor.listener;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import xyz.nkomarn.Harbor.task.Checker;
import xyz.nkomarn.Harbor.util.Message;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBedEnter(final PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }
        final World world = event.getPlayer().getWorld();
        if (morePlayerNeeded(world, 1)) {
            Message.SendChatMessage(world, "messages.chat.sleeping", event.getPlayer().getDisplayName(), 1);
            Message.SendActionbarMessage(world, "messages.actionbar.sleeping", event.getPlayer().getDisplayName(), 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBedLeave(final PlayerBedLeaveEvent event) {
        final World world = event.getPlayer().getWorld();
        if (Checker.isNight(world) && !Checker.skippingWorlds.contains(world) && morePlayerNeeded(world, 0)) {
            Message.SendChatMessage(world, "messages.chat.left", event.getPlayer().getDisplayName(), 0);
        }
    }

    private boolean morePlayerNeeded(final World world, final int change) {
        final int sleeping = Checker.getSleeping(world) + change;
        final int needed = Checker.getNeeded(world) - change;
        return sleeping > 0 && needed > 0;
    }
}
