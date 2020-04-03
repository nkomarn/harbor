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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BedListener implements Listener {
    private Map<UUID, Long> cooldowns = new HashMap<>(); // TODO, totally fucked right now (lol)

    @EventHandler(ignoreCancelled = true)
    public void onBedEnter(final PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
        if (Checker.skippingWorlds.contains(event.getPlayer().getWorld())) return;
        Bukkit.getScheduler().runTaskLater(Harbor.getHarbor(), () -> {
            UUID playerUuid = event.getPlayer().getUniqueId();
            if (cooldowns.containsKey(playerUuid) && !(cooldowns.get(playerUuid) +
                    (Config.getInteger("messages.chat.message-cooldown") * 1000) >= System.currentTimeMillis())) return;

            Messages.sendWorldChatMessage(event.getBed().getWorld(),
                    Config.getString("messages.chat.player-sleeping")
                            .replace("[player]", event.getPlayer().getName())
                            .replace("[displayname]", event.getPlayer().getDisplayName()));
            cooldowns.put(playerUuid, System.currentTimeMillis());
        }, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBedLeave(final PlayerBedLeaveEvent event) {
        if (Checker.skippingWorlds.contains(event.getPlayer().getWorld())) return;
        Bukkit.getScheduler().runTaskLater(Harbor.getHarbor(), () -> {
            UUID playerUuid = event.getPlayer().getUniqueId();
            if (cooldowns.containsKey(playerUuid) && !(cooldowns.get(playerUuid) +
                    (Config.getInteger("messages.chat.message-cooldown") * 1000) >= System.currentTimeMillis())) return;

            Messages.sendWorldChatMessage(event.getBed().getWorld(),
                    Config.getString("messages.chat.player-left-bed")
                            .replace("[player]", event.getPlayer().getName())
                            .replace("[displayname]", event.getPlayer().getDisplayName()));
            cooldowns.put(playerUuid, System.currentTimeMillis());
        }, 1);
    }
}
