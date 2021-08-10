/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.punishment

import java.util.Date
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import sh.foxboy.bapp.Constants
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.api.punishment.PunishmentResponse
import sh.foxboy.bapp.api.punishment.PunishmentType
import sh.foxboy.bapp.database.PostgresHandler.getLastPunishment
import sh.foxboy.bapp.database.PostgresHandler.getPunishmentById

class Punishment(private val type: PunishmentType, private val arbiter: OfflinePlayer, private val target: OfflinePlayer?, private var reason: String?, private var expiry: Date, private var appealed: Boolean = false) : Punishment,
    WithPlugin {

    private var id = plugin.postgresHandler.getLastId()
    private var tmpreason = reason ?: "You have been punished!"

    override fun commit(): PunishmentResponse {
        if (target == null)
            return PunishmentResponse.TARGET_NOT_EXIST

        if (getPunishmentById(id) != null)
            return PunishmentResponse.PUNISHMENT_ALREADY_PUSHED

        if (getLastPunishment(target.uniqueId) != null && getLastPunishment(target.uniqueId)?.isAppealed == false)
            return PunishmentResponse.TARGET_ALREADY_PUNISHED

        if (!checkTypePermission(arbiter, target, type))
            return PunishmentResponse.PERMISSION_DENIED

        plugin.postgresHandler.insertPunishment(this)
        return PunishmentResponse.OK
    }

    override fun getId(): Int {
        return this.id + 1
    }

    override fun getType(): PunishmentType {
        return this.type
    }

    override fun getArbiter(): OfflinePlayer {
        return this.arbiter
    }

    override fun getTarget(): OfflinePlayer? {
        return this.target
    }

    override fun getReason(): String {
        return this.tmpreason
    }

    override fun getExpiry(): Date {
        return this.expiry
    }

    override fun isAppealed(): Boolean {
        return this.appealed
    }

    /**
     * A crude check to see if the arbiter has the permissions to punish the target
     */
    private fun checkTypePermission(arbiter: OfflinePlayer, target: OfflinePlayer, type: PunishmentType) = when (type) {
        PunishmentType.BAN -> plugin.permission.playerHas(Bukkit.getWorlds()[0].name, arbiter, Constants.Permissions.BAN) && !plugin.permission.playerHas(Bukkit.getWorlds()[0].name, target, Constants.Permissions.BAN_IMMUNE)
        PunishmentType.MUTE -> plugin.permission.playerHas(Bukkit.getWorlds()[0].name, arbiter, Constants.Permissions.MUTE) && !plugin.permission.playerHas(Bukkit.getWorlds()[0].name, target, Constants.Permissions.MUTE_IMMUNE)
        PunishmentType.KICK -> plugin.permission.playerHas(Bukkit.getWorlds()[0].name, arbiter, Constants.Permissions.KICK) && !plugin.permission.playerHas(Bukkit.getWorlds()[0].name, target, Constants.Permissions.KICK_IMMUNE)
        PunishmentType.WARN -> plugin.permission.playerHas(Bukkit.getWorlds()[0].name, arbiter, Constants.Permissions.WARN) && !plugin.permission.playerHas(Bukkit.getWorlds()[0].name, target, Constants.Permissions.WARN_IMMUNE)
        else -> false
    }
}
