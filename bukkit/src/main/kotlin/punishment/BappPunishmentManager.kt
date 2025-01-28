/*
 * Copyright (c) 2022 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.punishment

import java.util.UUID
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.api.entity.Arbiter
import sh.foxboy.bapp.api.entity.User
import sh.foxboy.bapp.api.managers.PunishmentManager
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.api.punishment.PunishmentType
import sh.foxboy.bapp.api.punishment.SortBy
import sh.foxboy.bapp.entity.BappArbiter

class BappPunishmentManager : PunishmentManager, WithPlugin {
    val consoleArbiter = BappArbiter("CONSOLE", UUID(0, 0))

    override fun getConsoleArbiter(): Arbiter {
        return consoleArbiter
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
        return this.plugin.postgresHandler.getPunishments(order, page, pageSize)
    }

    override fun getPunishments(page: Int): List<Punishment> {
        return getPunishments(SortBy.DATE_ASC, page, 8)
    }

    override fun getPunishments(page: Int, pageSize: Int): List<Punishment> {
        return getPunishments(SortBy.DATE_ASC, page, pageSize)
    }

    override fun createPunishment(
        type: PunishmentType,
        arbiter: Arbiter,
        target: User?,
        reason: String,
        expiry: Long?
    ): Punishment {
        return BappPunishment(type, arbiter, target, reason, expiry)
    }

    override fun deletePunishment(punishment: Punishment): Punishment {
        TODO("Not yet implemented")
    }
}
