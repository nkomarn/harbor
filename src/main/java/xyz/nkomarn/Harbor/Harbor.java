package xyz.nkomarn.Harbor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.Harbor.command.HarborCommand;
import xyz.nkomarn.Harbor.command.SleepingCommand;
import xyz.nkomarn.Harbor.event.GUIEvent;
import xyz.nkomarn.Harbor.event.PlayerEvent;
import xyz.nkomarn.Harbor.event.SpawnEvent;
import xyz.nkomarn.Harbor.nms.NMSUtils;
import xyz.nkomarn.Harbor.task.Timer;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Counters;
import xyz.nkomarn.Harbor.util.Updater;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Harbor extends JavaPlugin {
    public static Harbor instance;
    public static String version = "1.5.2";
    public static boolean debug = false;
    public static boolean enabled = false;
    public static boolean prerelease = false;

    private Config config = new Config();
    private NMSUtils nms = new NMSUtils();

    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        nms.setNMS();

        final PluginManager pm = Bukkit.getPluginManager();
        Stream.of(
                new PlayerEvent(), new SpawnEvent(), new GUIEvent()
        ).forEach(l -> pm.registerEvents(l, this));
        getCommand("harbor").setExecutor(new HarborCommand());
        getCommand("sleeping").setExecutor(new SleepingCommand());

        Bukkit.getServer().getWorlds().forEach(w -> {
            ArrayList<Player> s = new ArrayList<>();
            Counters.sleeping.put(w, s);
            w.getPlayers().forEach(p -> {
                p.setPlayerListName(p.getName());
                Counters.activity.put(p, System.currentTimeMillis());
            });
        });

        int interval = config.getInteger("values.clock");
        if (enabled) Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this,
                new Timer(), 0L, interval * 20);
        if (getConfig().getBoolean("debug")) debug = true;
        if (enabled && this.getConfig().getBoolean("features.notifier")) {
            if (debug) Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    getConfig().getString("messages.miscellaneous.prefix")) + "Checking for new updates...");
            if (!prerelease) {
                Updater updater = new Updater();
                updater.check();
            }
        }
        if (prerelease) Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.miscellaneous.prefix") + "&cThis Harbor version is a prerelease. Not everything is guaranteed to work correctly, but the plugin should at least be stable. "
                + "If you encounter an issue, please create an issue on GitHub: &c&ohttps://github.com/nkomarn/Harbor/issues&c. You can download newer builds when they're available at &c&ohttps://harbor.nkomarn.xyz&c."));
    }

    public void onDisable() {
        // Nothing lol
    }
}
