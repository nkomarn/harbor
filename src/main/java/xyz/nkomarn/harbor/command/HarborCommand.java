package xyz.nkomarn.harbor.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.util.Config;

import java.util.Collections;
import java.util.List;

public class HarborCommand implements TabExecutor {

    private final Harbor harbor;
    private final Config config;

    public HarborCommand(@NotNull Harbor harbor) {
        this.harbor = harbor;
        this.config = harbor.getConfiguration();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1 || !sender.hasPermission("harbor.admin")) {
            sender.sendMessage(config.getPrefix() + "Harbor " + harbor.getVersion() + " by TechToolbox (@nkomarn).");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            config.reload();
            sender.sendMessage(config.getPrefix() + "Reloaded configuration.");
            return true;
        }

        sender.sendMessage(config.getPrefix() + config.getString("messages.miscellaneous.unrecognized-command"));
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

        return Collections.singletonList("reload");
    }
}
