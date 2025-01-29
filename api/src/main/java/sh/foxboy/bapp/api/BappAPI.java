/*
 * Copyright (c) 2020-2025 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import sh.foxboy.bapp.api.cache.Cache;
import sh.foxboy.bapp.api.entity.User;
import sh.foxboy.bapp.api.managers.PunishmentManager;
import sh.foxboy.bapp.api.punishment.Punishment;

/**
 * Represents a generic implementation of your plugin.
 */
public interface BappAPI {
    /**
     * Register the plugin service.
     *
     * @param plugin  The plugin registering the service
     * @param service The plugin's implementation of the service
     */
    static void registerService(JavaPlugin plugin, BappAPI service) {
        Bukkit
            .getServicesManager()
            .register(BappAPI.class, service, plugin, ServicePriority.Lowest);
    }

    /**
     * Fetch the instantiated plugin service object.
     *
     * @return {@link BappAPI}
     */
    @NotNull
    static BappAPI getService() {
        var provider = Bukkit
            .getServicesManager()
            .getRegistration(BappAPI.class);
        if (provider == null) {
            throw new RuntimeException(
                "Cannot access API service - has not been registered!"
            );
        }
        return provider.getProvider();
    }

    /**
     * Return a reference to the plugin providing the Bapp implementation.
     *
     * @return {@link Plugin}
     */
    @NotNull
    Plugin getProvider();

    /**
     * Returns a PunishmentManager which can be used to create and manage punishments
     * @return {@link PunishmentManager}
     */
    @NotNull
    PunishmentManager getPunishmentManagerExplicit();

    /**
     * Returns a cache of users
     * @return {@link Cache<User>}
     */
    @NotNull
    Cache<User> getUserCache();

    /**
     * Returns a cache of punishments
     * @return {@link Cache<Punishment>}
     */
    @NotNull
    Cache<Punishment> getPunishmentCache();
}
