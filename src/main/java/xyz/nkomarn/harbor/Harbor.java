package xyz.nkomarn.harbor;

import com.earth2me.essentials.Essentials;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.api.AFKProvider;
import xyz.nkomarn.harbor.api.ExclusionProvider;
import xyz.nkomarn.harbor.api.LogicType;
import xyz.nkomarn.harbor.command.ForceSkipCommand;
import xyz.nkomarn.harbor.command.HarborCommand;
import xyz.nkomarn.harbor.listener.BedListener;
import xyz.nkomarn.harbor.task.Checker;
import xyz.nkomarn.harbor.util.Config;
import xyz.nkomarn.harbor.util.Messages;
import xyz.nkomarn.harbor.util.Metrics;
import xyz.nkomarn.harbor.util.PlayerManager;

import java.util.Arrays;
import java.util.Optional;

public class Harbor extends JavaPlugin {
    private Config config;
    private Checker checker;
    private Messages messages;
    private PlayerManager playerManager;
    private Essentials essentials;

    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        config = new Config(this);
        checker = new Checker(this);
        messages = new Messages(this);
        playerManager = new PlayerManager(this);
        essentials = (Essentials) pluginManager.getPlugin("Essentials");

        Arrays.asList(
                messages,
                playerManager,
                new BedListener(this)
        ).forEach(listener -> pluginManager.registerEvents(listener, this));

        getCommand("harbor").setExecutor(new HarborCommand(this));
        getCommand("forceskip").setExecutor(new ForceSkipCommand(this));



        if (config.getBoolean("metrics")) {
            new Metrics(this);
        }
    }



    @Override
    public void onDisable() {
        for (World world : getServer().getWorlds()) {
            messages.clearBar(world);
        }
    }

    @NotNull
    public String getVersion() {
        return getDescription().getVersion();
    }

    @NotNull
    public Config getConfiguration() {
        return config;
    }

    @NotNull
    public Checker getChecker() {
        return checker;
    }

    @NotNull
    public Messages getMessages() {
        return messages;
    }

    @NotNull
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * Add an {@link ExclusionProvider} to harbor, so an external plugin can set a player to be excluded from the sleep count
     *
     * @param provider An external implementation of an {@link ExclusionProvider}, provided by an implementing plugin
     *
     * @see ExclusionProvider
     * @see Checker#addExclusionProvider(ExclusionProvider)
     */
    @SuppressWarnings("unused")
    public void addExclusionProvider(ExclusionProvider provider) {
        checker.addExclusionProvider(provider);
    }

    /**
     * Remove an {@link ExclusionProvider}, for use by an external plugin
     *
     * @param provider The provider to remove
     *
     * @see #addExclusionProvider(ExclusionProvider)
     */
    @SuppressWarnings("unused")
    public void removeExclusionProvider(ExclusionProvider provider){
        checker.removeExclusionProvider(provider);
    }

    /**
     * Add an {@link AFKProvider} to harbor, so an external plugin can provide an AFK status to harbor
     *
     * @param provider An external implementation of an {@link AFKProvider}, provided by an implementing plugin
     *
     * @see AFKProvider
     * @see PlayerManager#addAfkProvider(AFKProvider, LogicType)
     */
    @SuppressWarnings("unused")
    public void addAFKProvider(AFKProvider provider, LogicType type) {
        playerManager.addAfkProvider(provider, type);
    }

    /**
     * Removes an {@link ExclusionProvider}
     * @param provider The provider to remove
     *
     * @see #addAFKProvider(AFKProvider, LogicType)
     */
    @SuppressWarnings("unused")
    public void removeAFKProvider(AFKProvider provider){
        playerManager.removeAfkProvider(provider);
    }

    /**
     * @return The current instance of Essentials ({@link Essentials}, wrapped in {@link Optional}
     */
    @NotNull
    public Optional<Essentials> getEssentials() {
        return Optional.ofNullable(essentials);
    }
}
