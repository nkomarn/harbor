package xyz.nkomarn.harbor.task;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.api.ExclusionProvider;
import xyz.nkomarn.harbor.provider.GameModeExclusionProvider;
import xyz.nkomarn.harbor.util.Config;
import xyz.nkomarn.harbor.util.Messages;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class Checker extends BukkitRunnable implements Listener {
    private final Set<ExclusionProvider> providers;
    private final Harbor harbor;
    private final Set<UUID> skippingWorlds;

    public Checker(@NotNull Harbor harbor) {
        this.harbor = harbor;
        this.skippingWorlds = new HashSet<>();
        this.providers = new HashSet<>();

        providers.add(new GameModeExclusionProvider(harbor));
        providers.add(player -> harbor.getConfig().getBoolean("exclusions.ignored-permission", true) && player.hasPermission("harbor.ignored"));
        providers.add(player -> harbor.getConfig().getBoolean("exclusions.exclude-vanished", false) && isVanished(player));
        providers.add(player -> harbor.getConfig().getBoolean("exclusions.exclude-afk", false) && harbor.getPlayerManager().isAfk(player));

        syncAllWorldsTime(false);

        int interval = Math.max(1, harbor.getConfiguration().getInteger("interval"));
        runTaskTimer(harbor, 20L, interval * 20L);
    }

    @Override
    public void run() {
        syncAllWorldsTime(false);

        Set<UUID> processedGroups = new HashSet<>();
        for (World world : Bukkit.getWorlds()) {
            if (processedGroups.contains(world.getUID())) {
                continue;
            }
            List<World> group = getSyncedWorlds(world);
            group.forEach(w -> processedGroups.add(w.getUID()));

            World representative = getRepresentativeWorld(world);
            if (validateWorld(representative)) {
                checkWorld(representative);
            }
        }
    }

    @EventHandler
    public void onWorldLoad(@NotNull WorldLoadEvent event) {
        if (isSyncEnabled() && !isBlacklisted(event.getWorld())) {
            World master = getMasterWorld();
            if (master != null && !master.getUID().equals(event.getWorld().getUID())) {
                event.getWorld().setTime(master.getTime());
            }
        }
    }

    public boolean isSyncEnabled() {
        return harbor.getConfiguration().getBoolean("sync-worlds.enabled");
    }

    public World getMasterWorld() {
        String masterName = harbor.getConfiguration().getString("sync-worlds.master-world");
        if (!masterName.isEmpty()) {
            World master = Bukkit.getWorld(masterName);
            if (master != null && !isBlacklisted(master)) {
                return master;
            }
        }
        return Bukkit.getWorlds().stream()
                .filter(w -> !isBlacklisted(w))
                .findFirst()
                .orElse(null);
    }

    @NotNull
    public World getRepresentativeWorld(@NotNull World world) {
        if (isSyncEnabled() && !isBlacklisted(world)) {
            World master = getMasterWorld();
            if (master != null) {
                return master;
            }
        }
        return world;
    }

    @NotNull
    public List<World> getSyncedWorlds(@NotNull World world) {
        if (isSyncEnabled() && !isBlacklisted(world)) {
            List<World> synced = Bukkit.getWorlds().stream()
                    .filter(w -> !isBlacklisted(w))
                    .collect(toList());
            if (!synced.isEmpty()) {
                return synced;
            }
        }
        return Collections.singletonList(world);
    }

    public void syncAllWorldsTime(boolean force) {
        if (!isSyncEnabled()) {
            return;
        }
        World master = getMasterWorld();
        if (master == null || skippingWorlds.contains(master.getUID())) {
            return;
        }

        long masterTime = master.getTime();
        for (World w : getSyncedWorlds(master)) {
            if (w.getUID().equals(master.getUID())) {
                continue;
            }
            if (force || Math.abs(w.getTime() - masterTime) > 20L) {
                w.setTime(masterTime);
            }
        }
    }
    private boolean validateWorld(@NotNull World world) {
        return !skippingWorlds.contains(world.getUID())
                && !isBlacklisted(world)
                && isNight(world);
    }

    private void checkWorld(@NotNull World world) {
        Config config = harbor.getConfiguration();
        Messages messages = harbor.getMessages();

        int sleeping = getSleepingPlayers(world).size();
        int needed = getNeeded(world);

        if (sleeping < 1) {
            messages.clearBar(world);
            return;
        }

        if (needed > 0) {
            double sleepingPercentage = Math.min(1, (double) sleeping / getSkipAmount(world));

            messages.sendActionBarMessage(world, config.getString("messages.actionbar.players-sleeping"));
            messages.sendBossBarMessage(world, config.getString("messages.bossbar.players-sleeping.message"),
                    config.getString("messages.bossbar.players-sleeping.color"), sleepingPercentage);
        } else if (needed == 0) {
            messages.sendActionBarMessage(world, config.getString("messages.actionbar.night-skipping"));
            messages.sendBossBarMessage(world, config.getString("messages.bossbar.night-skipping.message"),
                    config.getString("messages.bossbar.night-skipping.color"), 1);

            if (!config.getBoolean("night-skip.enabled")) {
                return;
            }

            List<World> targetWorlds = getSyncedWorlds(world);

            if (config.getBoolean("night-skip.instant-skip")) {
                long dayTicks = config.getInteger("night-skip.daytime-ticks");
                targetWorlds.forEach(w -> w.setTime(dayTicks));
                clearWeather(world);
                resetStatus(world);
                return;
            }

            targetWorlds.forEach(w -> skippingWorlds.add(w.getUID()));
            new AccelerateNightTask(harbor, this, world);
        }
    }

    private boolean isNight(@NotNull World world) {
        long time = world.getTime();
        int dayTime = Math.floorMod(harbor.getConfiguration().getInteger("night-skip.daytime-ticks"), 24000);
        long nightEnd = (dayTime > 12541) ? dayTime - 1 : 23458;
        return time >= 12541 && time <= nightEnd;
    }

    public boolean isBlacklisted(@NotNull World world) {
        boolean listed = harbor.getConfiguration().getStringList("blacklisted-worlds").contains(world.getName());
        if (harbor.getConfiguration().getBoolean("whitelist-mode")) {
            return !listed;
        }
        return listed;
    }

    public static boolean isVanished(@NotNull Player player) {
        return player.getMetadata("vanished").stream().anyMatch(MetadataValue::asBoolean);
    }

    public int getPlayers(@NotNull World world) {
        int totalPlayers = 0;
        int excludedPlayers = 0;
        for (World w : getSyncedWorlds(world)) {
            totalPlayers += w.getPlayers().size();
            excludedPlayers += getExcluded(w).size();
        }
        return Math.max(0, totalPlayers - excludedPlayers);
    }

    @NotNull
    public List<Player> getSleepingPlayers(@NotNull World world) {
        return getSyncedWorlds(world).stream()
                .flatMap(w -> w.getPlayers().stream())
                .filter(player -> player.getPose() == Pose.SLEEPING)
                .collect(toList());
    }

    public int getSkipAmount(@NotNull World world) {
        return (int) Math.ceil(getPlayers(world) * (harbor.getConfiguration().getDouble("night-skip.percentage") / 100));
    }

    public int getNeeded(@NotNull World world) {
        double percentage = harbor.getConfiguration().getDouble("night-skip.percentage");
        return Math.max(0, (int) Math.ceil((getPlayers(world)) * (percentage / 100) - getSleepingPlayers(world).size()));
    }

    @NotNull
    private List<Player> getExcluded(@NotNull World world) {
        return world.getPlayers().stream()
                .filter(this::isExcluded)
                .collect(toList());
    }

    private boolean isExcluded(@NotNull Player player) {
        return providers.stream().anyMatch(provider -> provider.isExcluded(player));
    }

    public boolean isSkipping(@NotNull World world) {
        return getSyncedWorlds(world).stream().anyMatch(w -> skippingWorlds.contains(w.getUID()));
    }

    public void forceSkip(@NotNull World world) {
        World rep = getRepresentativeWorld(world);
        getSyncedWorlds(rep).forEach(w -> skippingWorlds.add(w.getUID()));
        new AccelerateNightTask(harbor, this, rep);
    }

    public void resetStatus(@NotNull World world) {
        List<World> targetWorlds = getSyncedWorlds(world);
        wakeUpPlayers(world);
        harbor.getServer().getScheduler().runTaskLater(harbor, () -> {
            targetWorlds.forEach(w -> skippingWorlds.remove(w.getUID()));
            harbor.getPlayerManager().clearCooldowns();
            harbor.getMessages().sendRandomChatMessage(world, "messages.chat.night-skipped");
            harbor.getMessages().clearBar(world);
        }, 20L);
    }

    public void wakeUpPlayers(@NotNull World world) {
        ensureMain(() -> getSyncedWorlds(world).forEach(w -> w.getPlayers().stream()
                .filter(LivingEntity::isSleeping)
                .forEach(player -> player.wakeup(true))));
    }

    public void clearWeather(@NotNull World world) {
        ensureMain(() -> {
            Config config = harbor.getConfiguration();
            for (World w : getSyncedWorlds(world)) {
                if (w.hasStorm() && config.getBoolean("night-skip.clear-rain")) {
                    w.setStorm(false);
                }
                if (w.isThundering() && config.getBoolean("night-skip.clear-thunder")) {
                    w.setThundering(false);
                }
            }
        });
    }

    public void ensureMain(@NotNull Runnable runnable) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(harbor, runnable);
        } else {
            runnable.run();
        }
    }

    public void addExclusionProvider(ExclusionProvider provider) {
        providers.add(provider);
    }

    public void removeExclusionProvider(ExclusionProvider provider) {
        providers.remove(provider);
    }
}
