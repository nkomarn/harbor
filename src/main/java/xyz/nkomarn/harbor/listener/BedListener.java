package xyz.nkomarn.harbor.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.task.Checker;
import xyz.nkomarn.harbor.util.Config;
import xyz.nkomarn.harbor.util.PlayerManager;

import java.util.concurrent.TimeUnit;

public class BedListener implements Listener {

    private final Harbor harbor;
    private final PlayerManager playerManager;

    public BedListener(@NotNull Harbor harbor) {
        this.harbor = harbor;
        this.playerManager = harbor.getPlayerManager();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        Checker checker = harbor.getChecker();
        Player player = event.getPlayer();

        if (checker.isSkipping(player.getWorld())) {
            return;
        }

        if (checker.isVanished(player)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(harbor, () -> {
            Config config = harbor.getConfiguration();

            int cooldown = config.getInteger("messages.chat.message-cooldown");
            if (!(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - playerManager.getCooldown(player)) > cooldown)) {
                return;
            }

            playerManager.setCooldown(player, System.currentTimeMillis());
            harbor.getMessages().sendWorldChatMessage(event.getBed().getWorld(), config.getString("messages.chat.player-sleeping")
                    .replace("[player]", event.getPlayer().getName())
                    .replace("[displayname]", event.getPlayer().getDisplayName()));
        }, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBedLeave(PlayerBedLeaveEvent event) {
        Checker checker = harbor.getChecker();
        Player player = event.getPlayer();

        if (checker.isSkipping(player.getWorld())) {
            return;
        }

        if (checker.isVanished(player)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(harbor, () -> {
            Config config = harbor.getConfiguration();

            int cooldown = config.getInteger("messages.chat.message-cooldown");
            if (!(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - playerManager.getCooldown(player)) > cooldown)) {
                return;
            }

            playerManager.setCooldown(player, System.currentTimeMillis());
            harbor.getMessages().sendWorldChatMessage(event.getBed().getWorld(), config.getString("messages.chat.player-left-bed")
                    .replace("[player]", event.getPlayer().getName())
                    .replace("[displayname]", event.getPlayer().getDisplayName()));
        }, 1);
    }
}
