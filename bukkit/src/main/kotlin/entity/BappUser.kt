/*
 * Copyright (c) 2022 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.entity

import java.util.UUID
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.api.entity.Arbiter
import sh.foxboy.bapp.api.flag.BehaviorFlag
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.api.punishment.PunishmentResponse
import sh.foxboy.bapp.api.punishment.PunishmentType
import sh.foxboy.bapp.api.punishment.SortBy

class BappUser(private val name: String, private val uniqueId: UUID) : sh.foxboy.bapp.api.entity.User {

    private val manager = Bapp.plugin.getPunishmentManagerExplicit()

    override fun getName(): String {
        return this.name
    }

    override fun getUniqueId(): UUID {
        return this.uniqueId
    }

    override fun ban(reason: String, arbiter: Arbiter): PunishmentResponse {
        return manager.createPunishment(PunishmentType.BAN, arbiter, this, reason, Long.MAX_VALUE).commit()
    }

    override fun ban(reason: String, arbiter: Arbiter, expiry: Long?): PunishmentResponse {
        return manager.createPunishment(PunishmentType.BAN, arbiter, this, reason, expiry).commit()
    }

    override fun ban(reason: String, arbiter: Arbiter, expiry: Long?, flags: List<BehaviorFlag>?): PunishmentResponse {
        return manager.createPunishment(PunishmentType.BAN, arbiter, this, reason, expiry, flags).commit()
    }

    override fun mute(reason: String, arbiter: Arbiter): PunishmentResponse {
        return manager.createPunishment(PunishmentType.MUTE, arbiter, this, reason, Long.MAX_VALUE).commit()
    }

    override fun mute(reason: String, arbiter: Arbiter, expiry: Long?): PunishmentResponse {
        return manager.createPunishment(PunishmentType.MUTE, arbiter, this, reason, expiry).commit()
    }

    override fun mute(reason: String, arbiter: Arbiter, expiry: Long?, flags: List<BehaviorFlag>?): PunishmentResponse {
        return manager.createPunishment(PunishmentType.MUTE, arbiter, this, reason, expiry, flags).commit()
    }

    override fun warn(reason: String, arbiter: Arbiter): PunishmentResponse {
        return manager.createPunishment(PunishmentType.WARN, arbiter, this, reason, Long.MAX_VALUE).commit()
    }

    override fun warn(reason: String, arbiter: Arbiter, expiry: Long?): PunishmentResponse {
        return manager.createPunishment(PunishmentType.WARN, arbiter, this, reason, expiry).commit()
    }

    override fun warn(reason: String, arbiter: Arbiter, expiry: Long?, flags: List<BehaviorFlag>?): PunishmentResponse {
        return manager.createPunishment(PunishmentType.WARN, arbiter, this, reason, expiry, flags).commit()
    }

    override fun kick(reason: String, arbiter: Arbiter): PunishmentResponse {
        return manager.createPunishment(PunishmentType.KICK, arbiter, this, reason, Long.MAX_VALUE).commit()
    }

    override fun getPunishments(): List<Punishment> {
        return getPunishments(SortBy.DATE_ASC)
    }

    override fun getPunishments(sortBy: SortBy): List<Punishment> {
        return getPunishments(sortBy, 1)
    }

    override fun getPunishments(sortBy: SortBy, page: Int): List<Punishment> {
        return getPunishments(sortBy, page, 8)
    }

    override fun getPunishments(order: SortBy, page: Int, pageSize: Int): List<Punishment> {
        return Bapp.plugin.postgresHandler.getPunishments(order, page, pageSize, this)
    }

    override fun getPunishments(page: Int): List<Punishment> {
        return getPunishments(SortBy.DATE_ASC, page, 8)
    }

    override fun getPunishments(page: Int, pageSize: Int): List<Punishment> {
        return getPunishments(SortBy.DATE_ASC, page, pageSize)
    }

    override fun getKey(): String {
        return this.uniqueId.toString()
    }
}
