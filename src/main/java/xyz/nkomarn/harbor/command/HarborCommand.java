package xyz.nkomarn.harbor.command;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.task.Checker;
import xyz.nkomarn.harbor.util.Config;

import java.util.Arrays;
import java.util.List;

public class HarborCommand implements TabExecutor {

    private final Harbor harbor;

    public HarborCommand(@NotNull Harbor harbor) {
        this.harbor = harbor;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Config config = harbor.getConfiguration();
        String prefix = ChatColor.translateAlternateColorCodes('&', config.getString("messages.miscellaneous.chat-prefix"));

        if (args.length < 1 || !sender.hasPermission("harbor.admin")) {
            sender.sendMessage(prefix + "Harbor version " + harbor.getVersion() + " by TechToolbox (@nkomarn).");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            config.reload();
            sender.sendMessage(prefix + "Reloaded configuration.");
            return true;
        }

        if (args[0].equalsIgnoreCase("forceskip")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix + "This command can only be used by a player.");
                return true;
            }

            Player player = (Player) sender;
            World world = player.getWorld();
            Checker checker = harbor.getChecker();

            if (checker.isSkipping(world)) {
                sender.sendMessage(prefix + "This world's time is already being accelerated.");
            } else {
                checker.forceSkip(world);
                sender.sendMessage(prefix + "Forcing night skip in your world.");
            }

            return true;
        }

        sender.sendMessage(prefix + config.getString("messages.miscellaneous.unrecognized-command"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("harbor.admin")) {
            return null;
        }

        if (args.length != 1) {
            return null;
        }

        return Arrays.asList("forceskip", "reload");
    }
}
