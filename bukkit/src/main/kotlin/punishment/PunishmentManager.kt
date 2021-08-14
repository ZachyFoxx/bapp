package sh.foxboy.bapp.punishment

import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.selectAll
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.api.punishment.PunishmentManager
import sh.foxboy.bapp.api.punishment.PunishmentType
import sh.foxboy.bapp.api.punishment.SortBy
import sh.foxboy.bapp.database.tables.PunishmentsTable
import java.util.Date

class PunishmentManager : PunishmentManager, WithPlugin {
    override fun getPunishments(): List<Punishment> {
        return getPunishments(SortBy.DATE_ASC)
    }

    override fun getPunishments(sortBy: SortBy): List<Punishment> {
        return getPunishments(sortBy, 1)
    }

    override fun getPunishments(sortBy: SortBy, page: Int): List<Punishment> {
        return getPunishments(sortBy, 1, 8)
    }

    override fun getPunishments(order: SortBy, page: Int, pageSize: Int): List<Punishment> {
        return this.plugin.postgresHandler.getPunishments(PunishmentsTable.selectAll(), order, page, pageSize)
    }

    override fun getPunishments(page: Int): List<Punishment> {
        return getPunishments(SortBy.DATE_ASC, page, 8)
    }

    override fun getPunishments(page: Int, pageSize: Int): List<Punishment> {
        return getPunishments(SortBy.DATE_ASC, page, pageSize)
    }

    override fun createPunishment(
        type: PunishmentType,
        arbiter: OfflinePlayer,
        target: OfflinePlayer?,
        reason: String,
        expiry: Date
    ): Punishment {
        return Punishment(type, arbiter, target, reason, expiry)
    }

    override fun deletePunishment(punishment: Punishment): Punishment {
        TODO("Not yet implemented")
    }
}