/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.util

import java.io.File
import net.milkbowl.vault.permission.Permission
import org.bukkit.Server
import org.bukkit.plugin.RegisteredServiceProvider
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.commands.debugPunishCommand

object StartupUtil : WithPlugin {

    fun registerCommands() {
        // TODO commands
        debugPunishCommand.register()
    }

    fun setupConfig(): Boolean {
        try {
            if (!plugin.dataFolder.exists()) {
                plugin.logger.info("Error: No folder was found! Creating...")
                if (!plugin.dataFolder.mkdirs()) {
                    plugin.logger.info("Error: Unable to create data folder, are your file permissions correct?")
                    return false
                }
                plugin.saveResource("config.yml", false)
                plugin.logger.info("The folder was created successfully!")
            }
            if (!File(plugin.dataFolder.absolutePath + "config.yml").exists()) {
                plugin.saveResource("config.yml", false)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
