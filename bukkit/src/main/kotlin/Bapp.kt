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
import sh.foxboy.bapp.api.cache.Cache
import sh.foxboy.bapp.api.entity.User
import sh.foxboy.bapp.api.managers.PunishmentManager
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.cache.BappCache
import sh.foxboy.bapp.database.PostgresHandler
import sh.foxboy.bapp.util.StartupUtil
import sh.foxboy.bapp.util.StartupUtil.registerCommands

@PluginMain
class  Bapp : JavaPlugin(), BappAPI {

    companion object {
        lateinit var plugin: Bapp
    }

    lateinit var permission: Permission
    lateinit var postgresHandler: PostgresHandler

    private lateinit var userCache: Cache<User>
    private lateinit var punishmentCache: Cache<Punishment>

    override fun getProvider(): Plugin {
        return this
    }

    override fun onLoad() {
        plugin = this
        userCache = BappCache(User::class)
        punishmentCache = BappCache(Punishment::class)

        if (!StartupUtil.setupConfig()) throw RuntimeException("There was an error setting up the configuration")

        (userCache as BappCache<User>).ttl = config.getLong("cache.user.ttl", 86400L)
        (userCache as BappCache<User>).maxSize = config.getInt("cache.user.entry-count", 1000)

        (punishmentCache as BappCache<Punishment>).ttl = config.getLong("cache.user.entry-count", 43200L)
        (punishmentCache as BappCache<Punishment>).maxSize = config.getInt("cache.punishment.entry-count", 500)

        postgresHandler = PostgresHandler()

        if (!postgresHandler.init()) throw RuntimeException("There was an error setting up the database")

        CommandAPI.onLoad(CommandAPIConfig().verboseOutput(true))
        registerCommands()
    }

    override fun onEnable() {
        CommandAPI.onEnable(this)
        BappAPI.registerService(this, this)
        permission = server.servicesManager.getRegistration(Permission::class.java)?.provider ?: throw RuntimeException("No permission provider not found!")

        logger.info("$name ${description.version} enabled successfully!")
    }

    override fun onDisable() {
        reloadConfig()
    }

    override fun getPunishmentManager(): PunishmentManager {
        return this.punishmentManager
    }

    override fun getUserCache(): Cache<User> {
        return this.userCache
    }

    override fun getPunishmentCache(): Cache<Punishment> {
        return this.punishmentCache
    }
}
