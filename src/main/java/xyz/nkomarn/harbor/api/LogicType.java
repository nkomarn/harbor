package xyz.nkomarn.harbor.api;

import org.bukkit.configuration.Configuration;
import org.jetbrains.annotations.NotNull;

/**
 * An enum to represent the type of logic to be used when combining multiple Providers
 */
public enum LogicType {
    AND,

    @SuppressWarnings("unused")
    OR;

    public static LogicType fromConfig(@NotNull Configuration configuration, String path, LogicType defaultType) {
        return valueOf(configuration.getString(path, defaultType.toString()).toUpperCase().trim());
    }
}
