/*
 * Copyright (c) 2020-2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a generic implementation of your plugin.
 */
public interface BappAPI {
    /**
     * Register the chat service.
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
     * Fetch the instantiated chat service object.
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
    Plugin getProvider();
}
