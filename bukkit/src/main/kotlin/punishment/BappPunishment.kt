/*
 * Copyright (c) 2021-2022 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.punishment

import java.lang.Exception
import org.bukkit.Bukkit
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.api.entity.Arbiter
import sh.foxboy.bapp.api.entity.User
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.api.punishment.PunishmentResponse
import sh.foxboy.bapp.api.punishment.PunishmentType

class BappPunishment(private val type: PunishmentType, private val arbiter: Arbiter, private val target: User?, private var reason: String?, private var expiry: Long?, private var appealed: Boolean = false, private var id: Int = Bapp.plugin.postgresHandler.getLastId() + 1) : Punishment,
    WithPlugin {

    private var tmpreason = reason ?: "You have been punished!"

    override fun getKey(): String {
        return this.id.toString()
    }

    override fun toString(): String {
        val arbiterName = arbiter.name
        val arbiterUuid = arbiter.uniqueId
        val targetName = target?.name
        val targetUuid = target?.uniqueId

        return "BappPunishment(type=$type, arbiter=Arbiter($arbiterName, $arbiterUuid, target=Target($targetName, $targetUuid), reason=$reason, expiry=$expiry, appealed=$appealed, id=$id)"
    }

    override fun commit(): PunishmentResponse {
        try {
            if (target == null)
                return PunishmentResponse.TARGET_NOT_EXIST

            if (plugin.postgresHandler.getPunishmentById(id) != null)
                return PunishmentResponse.PUNISHMENT_ALREADY_PUSHED

            if ((plugin.postgresHandler.getLastPunishment(target.uniqueId) != null) && (plugin.postgresHandler.getLastPunishment(
                    target.uniqueId
                )?.isAppealed == false)
            )
                return PunishmentResponse.TARGET_ALREADY_PUNISHED

            if (!plugin.permission.playerHas(Bukkit.getWorlds()[0].name, Bukkit.getOfflinePlayer(arbiter.uniqueId), "${Constants.Permissions.PREFIX}.$type"))
                return PunishmentResponse.PERMISSION_DENIED

            if (plugin.permission.playerHas(Bukkit.getWorlds()[0].name, Bukkit.getOfflinePlayer(target.uniqueId), "${Constants.Permissions.PREFIX}.$type.immune"))
                return PunishmentResponse.TARGET_IMMUNE

            plugin.postgresHandler.insertPunishment(this)
            return PunishmentResponse.OK
        } catch (e: Exception) {
            e.printStackTrace()
            return PunishmentResponse.SERVER_ERROR
        }
    }

    override fun getId(): Int {
        return this.id + 1
    }

    override fun getType(): PunishmentType {
        return this.type
    }

    override fun getArbiter(): Arbiter {
        return this.arbiter
    }

    override fun getTarget(): User? {
        return this.target
    }

    override fun getReason(): String {
        return this.tmpreason
    }

    override fun getExpiry(): Long? {
        return this.expiry
    }

    override fun isAppealed(): Boolean {
        return this.appealed
    }
}
