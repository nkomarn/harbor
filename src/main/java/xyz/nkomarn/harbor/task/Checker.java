package xyz.nkomarn.harbor.task;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.util.Config;
import xyz.nkomarn.harbor.util.Messages;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

public class Checker extends BukkitRunnable {

    private final Harbor harbor;
    private final Set<UUID> skippingWorlds;

    public Checker(@NotNull Harbor harbor) {
        this.harbor = harbor;
        this.skippingWorlds = new HashSet<>();

        runTaskTimerAsynchronously(harbor, 0L, harbor.getConfiguration().getInteger("interval") * 20);
    }

    @Override
    public void run() {
        Bukkit.getWorlds().stream()
                .filter(this::validateWorld)
                .forEach(this::checkWorld);
    }

    /**
     * Checks if a given world is applicable for night skipping.
     *
     * @param world The world to check.
     * @return Whether Harbor should run the night skipping check below.
     */
    private boolean validateWorld(@NotNull World world) {
        return !skippingWorlds.contains(world.getUID())
                && !isBlacklisted(world)
                && isNight(world);
    }

    /**
     * Checks if enough people are sleeping, and in the case there are, starts the night skip task.
     *
     * @param world The world to check.
     */
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

            if (config.getBoolean("night-skip.instant-skip")) {
                messages.sendRandomChatMessage(world, "messages.chat.night-skipped");
                Bukkit.getScheduler().runTask(harbor, () -> world.setTime(config.getInteger("night-skip.daytime-ticks")));
                return;
            }

            skippingWorlds.add(world.getUID());
            new AccelerateNightTask(harbor, this, world);
        }
    }

    /**
     * Checks if the time in a given world is considered to be night.
     *
     * @param world The world to check.
     * @return Whether it is currently night in the provided world.
     */
    private boolean isNight(@NotNull World world) {
        return world.getTime() > 12950 || world.getTime() < 23950;
    }

    /**
     * Checks if a current world has been blacklisted (or whitelisted) in the configuration.
     *
     * @param world The world to check.
     * @return Whether a world is excluded from Harbor checks.
     */
    public boolean isBlacklisted(@NotNull World world) {
        boolean blacklisted = harbor.getConfiguration().getStringList("blacklisted-worlds").contains(world.getName());

        if (harbor.getConfiguration().getBoolean("whitelist-mode")) {
            return !blacklisted;
        }

        return blacklisted;
    }

    /**
     * Checks if a given player is in a vanished state.
     *
     * @param player The player to check.
     * @return Whether the provided player is vanished.
     */
    public boolean isVanished(@NotNull Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the amount of players that should be counted for Harbor's checks, ignoring excluded players.
     *
     * @param world The world for which to check player count.
     * @return The amount of players in a given world, minus excluded players.
     */
    public int getPlayers(@NotNull World world) {
        return Math.max(0, world.getPlayers().size() - getExcluded(world).size());
    }

    /**
     * Returns a list of all sleeping players in a given world.
     *
     * @param world The world in which to check for sleeping players.
     * @return A list of all currently sleeping players in the provided world.
     */
    @NotNull
    public List<Player> getSleepingPlayers(@NotNull World world) {
        return world.getPlayers().stream()
                .filter(Player::isSleeping)
                .collect(toList());
    }

    /**
     * Returns the amount of players that must be sleeping to skip the night in the given world.
     *
     * @param world The world for which to check skip amount.
     * @return The amount of players that need to sleep to skip the night.
     */
    public int getSkipAmount(@NotNull World world) {
        return (int) Math.ceil(getPlayers(world) * (harbor.getConfiguration().getDouble("night-skip.percentage") / 100));
    }

    /**
     * Returns the amount of players that are still needed to skip the night in a given world.
     *
     * @param world The world for which to check the amount of needed players.
     * @return The amount of players that still need to get into bed to start the night skipping task.
     */
    public int getNeeded(@NotNull World world) {
        double percentage = harbor.getConfiguration().getDouble("night-skip.percentage");
        return Math.max(0, (int) Math.ceil((getPlayers(world)) * (percentage / 100) - getSleepingPlayers(world).size()));
    }

    /**
     * Returns a list of players that are considered to be excluded from Harbor's player count checks.
     *
     * @param world The world for which to check for excluded players.
     * @return A list of excluded players in the given world.
     */
    @NotNull
    private List<Player> getExcluded(@NotNull World world) {
        return world.getPlayers().stream()
                .filter(this::isExcluded)
                .collect(toList());
    }

    /**
     * Checks if a given player is considered excluded from Harbor's checks.
     *
     * @param player The player to check.
     * @return Whether the given player is excluded.
     */
    private boolean isExcluded(@NotNull Player player) {
        ConfigurationSection exclusions = harbor.getConfig().getConfigurationSection("exclusions");

        if (exclusions == null) {
            return false;
        }

        boolean excludedByAdventure = exclusions.getBoolean("exclude-adventure", false) && player.getGameMode() == GameMode.ADVENTURE;
        boolean excludedByCreative = exclusions.getBoolean("exclude-creative", false) && player.getGameMode() == GameMode.CREATIVE;
        boolean excludedBySpectator = exclusions.getBoolean("exclude-spectator", false) && player.getGameMode() == GameMode.SPECTATOR;
        boolean excludedByPermission = exclusions.getBoolean("ignored-permission", false) && player.hasPermission("harbor.ignored");
        boolean excludedByVanish = exclusions.getBoolean("exclude-vanished", false) && isVanished(player);

        return excludedByAdventure
                || excludedByCreative
                || excludedBySpectator
                || excludedByPermission
                || excludedByVanish
                || harbor.getPlayerManager().isAfk(player);
    }

    /**
     * Checks whether the night is currently being skipped in the given world.
     *
     * @param world The world to check.
     * @return Whether the night is currently skipping in the provided world.
     */
    public boolean isSkipping(@NotNull World world) {
        return skippingWorlds.contains(world.getUID());
    }

    /**
     * Forces a world to begin skipping the night, skipping over the checks.
     *
     * @param world The world in which to force night skipping.
     */
    public void forceSkip(@NotNull World world) {
        skippingWorlds.add(world.getUID());
        new AccelerateNightTask(harbor, this, world);
    }

    /**
     * Resets the provided world to a non-skipping status.
     *
     * @param world The world for which to reset status.
     */
    public void resetStatus(@NotNull World world) {
        skippingWorlds.remove(world.getUID());
    }
}
