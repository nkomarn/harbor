package xyz.nkomarn.Harbor.listener;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import xyz.nkomarn.Harbor.task.Checker;
import xyz.nkomarn.Harbor.util.Messages;

public class PlayerListener implements Listener {

    // TODO Not sure if I will include this in final release

    /*@EventHandler(priority = EventPriority.HIGH)
    public void onBedEnter(final PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        boolean success = false; // 1.13 API change makes this necessary
        try {if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) success = true;}
        catch (NoSuchMethodError nme) {success = true;}

        if (success) {
            final World world = event.getPlayer().getWorld();

        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBedLeave(final PlayerBedLeaveEvent event) {
        final World world = event.getPlayer().getWorld();
        if (Checker.isNight(world) && !Checker.skippingWorlds.contains(world) && morePlayerNeeded(world, 0)) {
            Message.sendChatMessage(world, "messages.chat.left", event.getPlayer().getDisplayName(), 0);
        }
    }*/

}
