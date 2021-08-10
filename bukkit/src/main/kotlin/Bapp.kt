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
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import sh.foxboy.bapp.api.BappAPI
import sh.foxboy.bapp.database.PostgresHandler

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
    }

    override fun onEnable() {
        CommandAPI.onEnable(this)
        BappAPI.registerService(this, this)
        setupPermissions()
        logger.info("Hello!")
    }

    override fun onDisable() {
        reloadConfig()
    }

    private fun setupPermissions(): Boolean {
        val rsp: RegisteredServiceProvider<Permission> = server.servicesManager.getRegistration(Permission::class.java) ?: return false
        this.permission = rsp.provider
        return true
    }
}
