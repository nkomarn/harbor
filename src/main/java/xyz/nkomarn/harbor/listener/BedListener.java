package xyz.nkomarn.harbor.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.util.Messages;
import xyz.nkomarn.harbor.util.PlayerManager;

import java.util.concurrent.TimeUnit;

public class BedListener implements Listener {

    private final Harbor harbor;
    private final Messages messages;
    private final PlayerManager playerManager;

    public BedListener(@NotNull Harbor harbor) {
        this.harbor = harbor;
        this.messages = harbor.getMessages();
        this.playerManager = harbor.getPlayerManager();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        Player player = event.getPlayer();
        if (isMessageSilenced(player)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(harbor, () -> {
            playerManager.setCooldown(player, System.currentTimeMillis());
            harbor.getMessages().sendWorldChatMessage(event.getBed().getWorld(), messages.prepareMessage(
                    player, harbor.getConfiguration().getString("messages.chat.player-sleeping"))
            );
        }, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBedLeave(PlayerBedLeaveEvent event) {
        if (isMessageSilenced(event.getPlayer())) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(harbor, () -> {
            playerManager.setCooldown(event.getPlayer(), System.currentTimeMillis());
            harbor.getMessages().sendWorldChatMessage(event.getBed().getWorld(), messages.prepareMessage(
                    event.getPlayer(), harbor.getConfiguration().getString("messages.chat.player-left-bed"))
            );
        }, 1);
    }

    /**
     * Checks if a message should be silenced from chat (i.e. if the player is under cooldown).
     *
     * @param player The player context.
     * @return Whether the message should be silenced.
     */
    private boolean isMessageSilenced(@NotNull Player player) {
        if (harbor.getChecker().isSkipping(player.getWorld())) {
            return true;
        }

        if (harbor.getChecker().isVanished(player)) {
            return true;
        }

        int cooldown = harbor.getConfiguration().getInteger("messages.chat.message-cooldown");
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - playerManager.getCooldown(player)) < cooldown;
    }
}
