package xyz.nkomarn.Harbor;

import com.earth2me.essentials.Essentials;
import com.ohneemc.OhneeMC;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.Harbor.command.HarborCommand;
import xyz.nkomarn.Harbor.listener.AfkListener;
import xyz.nkomarn.Harbor.listener.BedListener;
import xyz.nkomarn.Harbor.task.Checker;
import xyz.nkomarn.Harbor.util.Config;
import xyz.nkomarn.Harbor.util.Metrics;

public class Harbor extends JavaPlugin {
    private static Harbor harbor;
    private static Essentials essentials;
    private static OhneeMC ohneemc;
    public static final String version = "1.6.2";

    public void onEnable() {
        harbor = this;
        saveDefaultConfig();
        new Metrics(this);

        getCommand("harbor").setExecutor(new HarborCommand());
        getServer().getPluginManager().registerEvents(new BedListener(), this);
        getServer().getScheduler().runTaskTimerAsynchronously(this,
                new Checker(), 0L, Config.getInteger("interval") * 20);

        essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        if (essentials == null) { // If Essentials isn't present, enable fallback AFK system
            getLogger().info("Essentials not present- registering fallback AFK detection system.");
            getServer().getPluginManager().registerEvents(new AfkListener(), this);
        }

        ohneemc = (OhneeMC) getServer().getPluginManager().getPlugin("OhneeMC");
        if (ohneemc == null) {
            getLogger().info("OhneeMC not present- registering fallback AFK detection system.");
            getServer().getPluginManager().registerEvents(new AfkListener(), this);
        }
    }

    public static Harbor getHarbor() {
        return harbor;
    }

    public static Essentials getEssentials() {
        return essentials;
    }

    public static OhneeMC getOhneemc() {
        return ohneemc;
    }
}
