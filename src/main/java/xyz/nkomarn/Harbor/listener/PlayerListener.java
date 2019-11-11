package xyz.nkomarn.Harbor.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.nkomarn.Harbor.Harbor;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Updater;
import java.util.concurrent.ExecutionException;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasPermission("harbor.admin") || !Config.getBoolean("features.notifier")) return;

        // Check for updates
        Bukkit.getScheduler().runTaskAsynchronously(Harbor.instance, () -> {
            boolean updateAvailable = false;
            try {
                updateAvailable = Updater.check().get();
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }

            if (updateAvailable) {
                TextComponent updateMessage = new TextComponent(ChatColor.translateAlternateColorCodes('&',
                        Config.getString("messages.miscellaneous.prefix")
                                + "&7Hey there, Harbor " + Updater.latest + " is now out!"
                                + " Click this message to upgrade automatically."));
                updateMessage.setColor(ChatColor.GRAY);
                updateMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("§a§l↑ §7Click to update Harbor now!").create()));
                updateMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/harbor update"));
                e.getPlayer().spigot().sendMessage(updateMessage);
            }
        });
    }
}
