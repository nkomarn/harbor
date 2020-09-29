package xyz.nkomarn.harbor.command;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.task.Checker;
import xyz.nkomarn.harbor.util.Config;

public class ForceSkipCommand implements CommandExecutor {

    private final Harbor harbor;
    private final Config config;

    public ForceSkipCommand(@NotNull Harbor harbor) {
        this.harbor = harbor;
        this.config = harbor.getConfiguration();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getPrefix() + "This command can only be used by a player.");
            return true;
        }

        Player player = (Player) sender;
        World world = player.getWorld();
        Checker checker = harbor.getChecker();

        if (checker.isSkipping(world)) {
            sender.sendMessage(config.getPrefix() + "This world's time is already being accelerated.");
        } else {
            sender.sendMessage(config.getPrefix() + "Forcing night skip in your world.");
            checker.forceSkip(world);
        }

        return true;
    }
}
