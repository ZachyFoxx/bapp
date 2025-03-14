/*
 * Copyright (c) 2021-2022 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.punishment

import java.lang.Exception
import java.util.UUID
import org.bukkit.Bukkit
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.api.entity.Arbiter
import sh.foxboy.bapp.api.entity.User
import sh.foxboy.bapp.api.flag.BehaviorFlag
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.api.punishment.PunishmentResponse
import sh.foxboy.bapp.api.punishment.PunishmentType
import sh.foxboy.bapp.util.TimeUtil

class BappPunishment(private val type: PunishmentType, private val arbiter: Arbiter, private val target: User?, private var reason: String?, private var expiry: Long?, private var appealed: Boolean = false, private var flags: List<BehaviorFlag>?, private var id: Int = Bapp.plugin.postgresHandler.getLastId() + 1, private var date: Long = System.currentTimeMillis()) : Punishment,
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

        return "BappPunishment(type=$type, arbiter=Arbiter($arbiterName, $arbiterUuid), target=Target($targetName, $targetUuid), reason=$reason, expiry=$expiry, appealed=$appealed, id=$id)"
    }

    override fun commit(): PunishmentResponse {
        try {
            // 1. Verify that the target exists.
            if (target == null) return PunishmentResponse.TARGET_NOT_EXIST

            // 2. Check if the target already has an active punishment of this type.
            if (hasActivePunishment(target.uniqueId, type))
                return PunishmentResponse.PUNISHMENT_ALREADY_PUSHED

            // 3. Verify that the punisher (arbiter) has permission to execute this punishment.
            if (!hasPunisherPermission(arbiter.uniqueId, type))
                return PunishmentResponse.PERMISSION_DENIED

            // 4. Check if the target is immune (unless the arbiter can bypass immunity).
            if (isTargetImmune(target.uniqueId, arbiter.uniqueId, type))
                return PunishmentResponse.TARGET_IMMUNE

            // 5. Process duration and insert punishment.
            if (arbiter != plugin.punishmentManagerExplicit.consoleArbiter) {
                val punisherPlayer = Bukkit.getPlayer(arbiter.uniqueId) ?: return PunishmentResponse.PERMISSION_DENIED
                val permanentAllowed = punisherPlayer.hasPermission("${Constants.Permissions.PREFIX}.group.unlimited")
                val maxDuration = when (type) {
                    PunishmentType.BAN -> TimeUtil.getMaxBanDurationForPlayer(punisherPlayer, plugin)
                    PunishmentType.MUTE -> TimeUtil.getMaxMuteDurationForPlayer(punisherPlayer, plugin)
                    else -> Long.MAX_VALUE
                }

                if (expiry == null) {
                    // Requested duration is permanent.
                    if (permanentAllowed) {
                        insertPunishment()
                        return PunishmentResponse.OK
                    } else {
                        return if (plugin.config.getBoolean("groups.reduce_to_limit")) {
                            this.expiry = System.currentTimeMillis() + maxDuration
                            insertPunishment()
                            PunishmentResponse.OK
                        } else {
                            PunishmentResponse.DURATION_EXCEEDS_PERMISSION
                        }
                    }
                } else {
                    val requestedDuration = System.currentTimeMillis() - expiry!!
                    if (requestedDuration > maxDuration && !permanentAllowed) {
                        return if (plugin.config.getBoolean("groups.reduce_to_limit")) {
                            this.expiry = System.currentTimeMillis() + maxDuration
                            insertPunishment()
                            PunishmentResponse.OK
                        } else {
                            PunishmentResponse.DURATION_EXCEEDS_PERMISSION
                        }
                    } else {
                        insertPunishment()
                        return PunishmentResponse.OK
                    }
                }
            } else {
                // Console arbiter bypasses duration and permission checks.
                insertPunishment()
                return PunishmentResponse.OK
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return PunishmentResponse.SERVER_ERROR
        }
    }

    fun announce(silent: Boolean, message: String) {
        // always send a message to sender regardless of permission to view silents
        Bukkit.getPlayer(arbiter.uniqueId)?.sendMessage(message)
        plugin.logger.info(message)

        if (config.getBoolean("global_broadcasts")) {
            // send over plugin message channel, with message and boolean
        }

        for (player in Bukkit.getOnlinePlayers()) {
            if (silent && !player.hasPermission("${Constants.Permissions.PREFIX}.announcements.silent")) continue
            if (player.uniqueId == arbiter.uniqueId) continue; // we've already sent the issuing user a message.
            player.sendMessage(message)
        }
    }

    // Helper to check if the target already has active punishments of the given type.
    private fun hasActivePunishment(targetId: UUID, type: PunishmentType): Boolean {
        return plugin.postgresHandler.getActivePunishments(targetId, type).isNotEmpty()
    }

    // Helper to verify the punisher's permission.
    private fun hasPunisherPermission(arbiterId: UUID, type: PunishmentType): Boolean {
        if (arbiterId == plugin.punishmentManagerExplicit.consoleArbiter.uniqueId)
            return true
        val worldName = Bukkit.getWorlds()[0].name
        val arbiterOfflinePlayer = Bukkit.getOfflinePlayer(arbiterId)
        return plugin.permission.playerHas(worldName, arbiterOfflinePlayer, "${Constants.Permissions.COMMAND_PREFIX}.$type")
    }

    // Helper to check if the target is immune unless bypassed.
    private fun isTargetImmune(targetId: UUID, arbiterId: UUID, type: PunishmentType): Boolean {
        if (arbiterId == plugin.punishmentManagerExplicit.consoleArbiter.uniqueId)
            return false
        try {
            val worldName = Bukkit.getWorlds()[0].name
            val targetOffline = Bukkit.getOfflinePlayer(targetId)
            val arbiterOffline = Bukkit.getOfflinePlayer(arbiterId)
            val immunePermission = "${Constants.Permissions.PREFIX}.immune.$type"
            val bypassPermission = "${Constants.Permissions.PREFIX}.bypass.$type"
            println(worldName)
            val test1 = plugin.permission.playerHas(worldName, targetOffline, immunePermission)
            val test2 = !plugin.permission.playerHas(worldName, arbiterOffline, bypassPermission)
            return test1 && test2
        } catch (e: Exception) {
            return false
        }
    }

    // Helper to insert the punishment into the database.
    private fun insertPunishment() {
        plugin.postgresHandler.insertPunishment(this)
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

    override fun getFlags(): List<BehaviorFlag>? {
        return this.flags
    }

    override fun getDate(): Long {
        return this.date
    }
}
