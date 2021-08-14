/*
 * Copyright (c) 2020-2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIConfig
import kr.entree.spigradle.annotations.PluginMain
import net.milkbowl.vault.permission.Permission
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import sh.foxboy.bapp.api.BappAPI
import sh.foxboy.bapp.database.PostgresHandler
import sh.foxboy.bapp.util.StartupUtil
import sh.foxboy.bapp.util.StartupUtil.registerCommands

@PluginMain
class Bapp : JavaPlugin(), BappAPI {

    companion object {
        lateinit var plugin: Bapp
    }

    lateinit var permission: Permission
    lateinit var postgresHandler: PostgresHandler

    override fun getProvider(): Plugin {
        return this
    }

    override fun onLoad() {
        plugin = this
        CommandAPI.onLoad(CommandAPIConfig().verboseOutput(true))

        if (!StartupUtil.setupConfig()) return

        postgresHandler = PostgresHandler()

        if (!postgresHandler.init()) return
        registerCommands()
    }

    override fun onEnable() {
        CommandAPI.onEnable(this)
        BappAPI.registerService(this, this)
        permission = server.servicesManager.getRegistration(Permission::class.java)?.provider ?: return

        logger.info("Bapp enabled successfully!")
    }

    override fun onDisable() {
        reloadConfig()
    }
}
