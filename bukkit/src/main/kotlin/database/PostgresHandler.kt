/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.time.Instant
import java.util.Date
import java.util.UUID
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import sh.foxboy.bapp.Constants
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.api.punishment.PunishmentType
import sh.foxboy.bapp.api.punishment.SortBy
import sh.foxboy.bapp.database.tables.PunishmentsTable
import sh.foxboy.bapp.database.tables.PunishmentsTable.appealed
import sh.foxboy.bapp.database.tables.PunishmentsTable.arbiterUniqueId
import sh.foxboy.bapp.database.tables.PunishmentsTable.expiry
import sh.foxboy.bapp.database.tables.PunishmentsTable.punishedAt
import sh.foxboy.bapp.database.tables.PunishmentsTable.reason
import sh.foxboy.bapp.database.tables.PunishmentsTable.targetUniqueId
import sh.foxboy.bapp.database.tables.PunishmentsTable.type

class PostgresHandler() : WithPlugin {
    lateinit var dbConnection: Database

    fun init(): Boolean {
        var success = true
        this.logger.info("[SQL] Checking SQL database has been set up correctly...")

        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://${
                config.getString(Constants.SettingsPaths.DATABASE_HOST)
            }:${
                config.getInt(Constants.SettingsPaths.DATABASE_PORT)
            }/${
                config.getString(Constants.SettingsPaths.DATABASE_DATABASE)
            }?sslmode=${config.getString(Constants.SettingsPaths.DATABASE_USE_SSL, "disabled")}"

            driverClassName = "sh.foxboy.bapp.libs.org.postgresql.Driver"
            username = config.getString(Constants.SettingsPaths.DATABASE_USERNAME, "postgres")!!
            password = config.getString(Constants.SettingsPaths.DATABASE_PASSWORD)!!
            maximumPoolSize = 2
        }

        val dataSource = HikariDataSource(config)
        dbConnection = Database.connect(dataSource)

        transaction(dbConnection) {
            try {
                addLogger(ExposedLogger())
                SchemaUtils.createMissingTablesAndColumns(PunishmentsTable)
            } catch (e: Exception) {
                logger.warning("[SQL] Failed to connect to SQL database - invalid connection info/database not up")
                success = false
            }
        }
        return dataSource.isRunning && success
    }

    /**************
     * BEGIN PUNISHMENT UTILS
     */

    fun getLastId(): Int {
        return transaction(dbConnection) {
            return@transaction PunishmentsTable.selectAll()
                .orderBy(PunishmentsTable.id to SortOrder.ASC)
                .lastOrNull()
                ?.getOrNull(PunishmentsTable.id) ?: 1
        }
    }

    fun getPunishmentById(id: Int): Punishment? {
        return transaction(dbConnection) {
            PunishmentsTable.select { (PunishmentsTable.id eq id) }
                .firstOrNull().let {
                    if (it == null) return@transaction null
                    return@transaction sh.foxboy.bapp.punishment.Punishment(PunishmentType.fromOrdinal(it[type]), Bukkit.getPlayer(it[arbiterUniqueId])!!, Bukkit.getPlayer(it[targetUniqueId]), it[reason], Date.from(Instant.ofEpochMilli(it[expiry])), it[appealed])
                }
        }
    }

    fun getLastPunishment(target: UUID): Punishment? {
        return transaction(dbConnection) {
            PunishmentsTable.select { (targetUniqueId eq target.toString()) }
                .firstOrNull().let {
                    if (it == null) return@transaction null
                    return@transaction sh.foxboy.bapp.punishment.Punishment(PunishmentType.fromOrdinal(it[type]), Bukkit.getPlayer(it[arbiterUniqueId])!!, Bukkit.getPlayer(it[targetUniqueId]), it[reason], Date.from(Instant.ofEpochMilli(it[expiry])), it[appealed])
                }
        }
    }

    fun insertPunishment(punishment: Punishment): Punishment {
        transaction(dbConnection) {
            PunishmentsTable.insert {
                it[type] = punishment.type.ordinal
                it[arbiterUniqueId] = punishment.arbiter.uniqueId.toString()
                it[targetUniqueId] = punishment.target!!.uniqueId.toString()
                it[expiry] = punishment.expiry.time
                it[reason] = punishment.reason
            }
        }
        return punishment
    }

    fun getPunishments(query: Query, sortBy: SortBy, page: Int, pageSize: Int): List<Punishment> {
        val punishments = mutableListOf<Punishment>()
        transaction(dbConnection) {
            query.limit(pageSize, ((page - 1) * pageSize).toLong())
                .orderBy(orderBy(sortBy))
                .iterator().forEach {
                    punishments.add(
                        sh.foxboy.bapp.punishment.Punishment(
                            PunishmentType.fromOrdinal(it[type]), Bukkit.getPlayer(it[arbiterUniqueId])!!, Bukkit.getPlayer(it[targetUniqueId]), it[reason], Date.from(Instant.ofEpochMilli(it[expiry]))
                        )
                    )
                }
        }
        return punishments.toList()
    }

    private fun orderBy(sortBy: SortBy): (Pair<Expression<*>, SortOrder>) = when (sortBy) {
        SortBy.DATE_ASC -> punishedAt to SortOrder.ASC
        SortBy.DATE_DESC -> punishedAt to SortOrder.DESC
        SortBy.EXPIRY_ASC -> expiry to SortOrder.ASC
        SortBy.EXPIRY_DESC -> expiry to SortOrder.DESC
        SortBy.USERNAME_ASC -> targetUniqueId to SortOrder.ASC
        SortBy.USERNAME_DESC -> targetUniqueId to SortOrder.ASC
    }


    /**************
     * END PUNISHMENT UTILS
     */
}
