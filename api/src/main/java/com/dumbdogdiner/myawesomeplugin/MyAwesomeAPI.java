/*
 * Copyright (c) 2020 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.myawesomeplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a generic implementation of your plugin.
 */
public interface MyAwesomeAPI {
  /**
   * Register the chat service.
   *
   * @param plugin  The plugin registering the service
   * @param service The plugin's implementation of the service
   */
  static void registerService(JavaPlugin plugin, MyAwesomeAPI service) {
    Bukkit
      .getServicesManager()
      .register(MyAwesomeAPI.class, service, plugin, ServicePriority.Lowest);
  }

  /**
   * Fetch the instantiated chat service object.
   *
   * @return {@link MyAwesomeAPI}
   */
  @NotNull
  static MyAwesomeAPI getService() {
    var provider = Bukkit
      .getServicesManager()
      .getRegistration(MyAwesomeAPI.class);
    if (provider == null) {
      throw new RuntimeException(
        "Cannot access API service - has not been registered!"
      );
    }
    return provider.getProvider();
  }

  /**
   * Return a reference to the plugin providing the MyAwesomeAPI implementation.
   *
   * @return {@link Plugin}
   */
  Plugin getProvider();

  /**
   * Get the welcome message implemented by this API.
   * @return {@link String}
   */
  String getWelcomeMessage();
}
