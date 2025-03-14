/*
 * Copyright (c) 2020-2022 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import java.util.UUID
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
import sh.foxboy.bapp.commands.punishment.banCommand
import sh.foxboy.bapp.database.PostgresHandler
import sh.foxboy.bapp.punishment.BappPunishmentManager
import sh.foxboy.bapp.util.StartupUtil
import sh.foxboy.bapp.util.StartupUtil.registerCommands
import sh.foxboy.bapp.utils.MessageFormatter

@PluginMain
class Bapp : JavaPlugin(), BappAPI {

    companion object {
        lateinit var plugin: Bapp
    }
    lateinit var messageFormatter: MessageFormatter
    lateinit var permission: Permission
    lateinit var postgresHandler: PostgresHandler
    var panic: Boolean = false

    private lateinit var userCache: Cache<User>
    private lateinit var punishmentCache: Cache<Punishment>
    private val punishmentManager: PunishmentManager by lazy {
        BappPunishmentManager() // This will only be initialized when first accessed, this prevents some fucky-wucky stuff with how we handle the Punishable entities.
    }

    override fun getProvider(): Plugin {
        return this
    }

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(true))
        plugin = this
        // this.punishmentManager = BappPunishmentManager()

        if (!StartupUtil.setupConfig()) throw RuntimeException("There was an error setting up the configuration")

        postgresHandler = PostgresHandler()
        if (!postgresHandler.init()) {
            panic = true
            throw RuntimeException("There was an error setting up the database")
        }

        userCache = BappCache(User::class)
        punishmentCache = BappCache(Punishment::class)

        (userCache as BappCache<User>).ttl = config.getLong("cache.user.ttl", 86400L)
        (userCache as BappCache<User>).maxSize = config.getInt("cache.user.entry-count", 1000)

        (punishmentCache as BappCache<Punishment>).ttl = config.getLong("cache.user.entry-count", 43200L)
        (punishmentCache as BappCache<Punishment>).maxSize = config.getInt("cache.punishment.entry-count", 500)

        messageFormatter = MessageFormatter(this)

        registerCommands()
    }

    override fun onEnable() {
        if (this.panic)
            throw RuntimeException("There was an error initializing the BAPP Plugin, please see logs for details.")

        CommandAPI.onEnable()
        BappAPI.registerService(this, this)
        permission = server.servicesManager.getRegistration(Permission::class.java)?.provider ?: throw RuntimeException("No permission provider not found!")

        CommandAPI.unregister("ban")
        banCommand.register()
        logger.info("$name ${description.version} enabled successfully!")
    }

    override fun onDisable() {
        reloadConfig()
    }

    override fun getPunishmentManagerExplicit(): PunishmentManager {
        return this.punishmentManager
    }

    override fun getUserCache(): Cache<User> {
        return this.userCache
    }

    override fun getPunishmentCache(): Cache<Punishment> {
        return this.punishmentCache
    }

    fun getUniqueId(): UUID {
        return UUID.fromString(this.getConfig().getString("uuid"))
    }
}
