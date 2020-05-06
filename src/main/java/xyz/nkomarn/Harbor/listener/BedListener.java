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
import java.util.concurrent.TimeUnit;

public class BedListener implements Listener {
    public static final Map<UUID, Long> COOLDOWNS = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onBedEnter(final PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
        if (Checker.SKIPPING_WORLDS.contains(event.getPlayer().getWorld())) return;
        if (Checker.isVanished(event.getPlayer())) return;

        Bukkit.getScheduler().runTaskLater(Harbor.getHarbor(), () -> {
            final UUID playerUuid = event.getPlayer().getUniqueId();
            if (!(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - getCooldown(playerUuid)) >
                    Config.getInteger("messages.chat.message-cooldown"))) return;

            Messages.sendWorldChatMessage(event.getBed().getWorld(),
                    Config.getString("messages.chat.player-sleeping")
                            .replace("[player]", event.getPlayer().getName())
                            .replace("[displayname]", event.getPlayer().getDisplayName()));
            COOLDOWNS.put(playerUuid, System.currentTimeMillis());
        }, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBedLeave(final PlayerBedLeaveEvent event) {
        if (Checker.SKIPPING_WORLDS.contains(event.getPlayer().getWorld())) return;
        if (Checker.isVanished(event.getPlayer())) return;

        Bukkit.getScheduler().runTaskLater(Harbor.getHarbor(), () -> {
            final UUID playerUuid = event.getPlayer().getUniqueId();
            if (!(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - getCooldown(playerUuid)) >
                    Config.getInteger("messages.chat.message-cooldown"))) return;

            Messages.sendWorldChatMessage(event.getBed().getWorld(),
                    Config.getString("messages.chat.player-left-bed")
                            .replace("[player]", event.getPlayer().getName())
                            .replace("[displayname]", event.getPlayer().getDisplayName()));
            COOLDOWNS.put(playerUuid, System.currentTimeMillis());
        }, 1);
    }

    private long getCooldown(final UUID playerUuid) {
        if (!COOLDOWNS.containsKey(playerUuid)) return 0;
        return COOLDOWNS.get(playerUuid);
    }
}
