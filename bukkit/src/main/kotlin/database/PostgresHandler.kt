/*
 * Copyright (c) 2021-2025 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.util.UUID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import sh.foxboy.bapp.Constants
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.api.entity.User
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.api.punishment.PunishmentType
import sh.foxboy.bapp.api.punishment.SortBy
import sh.foxboy.bapp.database.tables.AppealStatusesTable
import sh.foxboy.bapp.database.tables.AppealTable
import sh.foxboy.bapp.database.tables.PunishmentDataTable
import sh.foxboy.bapp.database.tables.PunishmentTypeTable
import sh.foxboy.bapp.database.tables.PunishmentsTable
import sh.foxboy.bapp.database.tables.PunishmentsTable.reason
import sh.foxboy.bapp.database.tables.ReputationFlagTypeTable
import sh.foxboy.bapp.database.tables.ServerGroupTypeTable
import sh.foxboy.bapp.database.tables.ServerGroupsTable
import sh.foxboy.bapp.database.tables.UserTable
import sh.foxboy.bapp.entity.BappArbiter
import sh.foxboy.bapp.entity.BappUser

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
            }"

            driverClassName = "org.postgresql.Driver"
            username = config.getString(Constants.SettingsPaths.DATABASE_USERNAME, "bapp")!!
            password = config.getString(Constants.SettingsPaths.DATABASE_PASSWORD, "bapp")!!
            maximumPoolSize = 10
        }

        val dataSource = HikariDataSource(config)
        dbConnection = Database.connect(dataSource)

        transaction(dbConnection) {
            try {
                addLogger(ExposedLogger())
                SchemaUtils.create(
                    AppealStatusesTable,
                    AppealTable,
                    PunishmentDataTable,
                    PunishmentsTable,
                    PunishmentTypeTable,
                    ReputationFlagTypeTable,
                    ServerGroupsTable,
                    ServerGroupTypeTable,
                    UserTable
                )
            } catch (e: Exception) {
                logger.warning("[SQL] Failed to connect to SQL database - invalid connection info/database not up (${e.cause})")
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
            val query = PunishmentsTable
            .join(UserTable, JoinType.INNER, onColumn = PunishmentsTable.userId, otherColumn = UserTable.uniqueId) // Join with targetUser
            .join(PunishmentDataTable, JoinType.INNER, onColumn = PunishmentsTable.id, otherColumn = PunishmentDataTable.punishmentTypeId) // Join with punishmentData
            .join(UserTable, JoinType.INNER, onColumn = PunishmentDataTable.issuedBy, otherColumn = UserTable.uniqueId) // Join with issuedByUser
            .join(PunishmentTypeTable, JoinType.INNER, onColumn = PunishmentsTable.punishmentTypeId, otherColumn = PunishmentTypeTable.id) // Join with punishmentType
            .selectAll()
            .where { PunishmentsTable.id eq id }
            .firstOrNull()

            // If a row is found, map it to the Punishment object
            query?.let { row ->
                // Get punishment type
                val punishmentType = PunishmentType.valueOf(row[PunishmentTypeTable.name])

                // Map the arbiter details
                val arbiter = BappArbiter(
                    name = row[UserTable.username],
                    uniqueId = row[PunishmentDataTable.issuedBy]
                )

                // Map the target user details
                val target = BappUser(
                    name = row[UserTable.username],
                    uniqueId = row[PunishmentsTable.userId]
                )

                // Return the punishment object
                return@transaction sh.foxboy.bapp.punishment.BappPunishment(
                    type = punishmentType,
                    arbiter = arbiter,
                    target = target,
                    reason = row[PunishmentsTable.reason],
                    expiry = row[PunishmentDataTable.endTime],
                    appealed = false,
                    id = row[PunishmentsTable.id]
                )
            }
        }
    }

    fun getLastPunishment(target: UUID): Punishment? {
        return transaction(dbConnection) {
            val query = PunishmentsTable
            .join(UserTable, JoinType.INNER, onColumn = PunishmentsTable.userId, otherColumn = UserTable.uniqueId) // Join with targetUser
            .join(PunishmentDataTable, JoinType.INNER, onColumn = PunishmentsTable.id, otherColumn = PunishmentDataTable.punishmentTypeId) // Join with punishmentData
            .join(UserTable, JoinType.INNER, onColumn = PunishmentDataTable.issuedBy, otherColumn = UserTable.uniqueId) // Join with issuedByUser
            .join(PunishmentTypeTable, JoinType.INNER, onColumn = PunishmentsTable.punishmentTypeId, otherColumn = PunishmentTypeTable.id) // Join with punishmentType
            .selectAll()
            .where { PunishmentsTable.userId eq target }
            .firstOrNull()

            // If a row is found, map it to the Punishment object
            query?.let { row ->
                // Get punishment type
                val punishmentType = PunishmentType.valueOf(row[PunishmentTypeTable.name])

                // Map the arbiter details
                val arbiter = BappArbiter(
                    name = row[UserTable.username],
                    uniqueId = row[PunishmentDataTable.issuedBy]
                )

                // Map the target user details
                val target2 = BappUser(
                    name = row[UserTable.username],
                    uniqueId = row[PunishmentsTable.userId]
                )

                // Return the punishment object
                return@transaction sh.foxboy.bapp.punishment.BappPunishment(
                    type = punishmentType,
                    arbiter = arbiter,
                    target = target2,
                    reason = row[PunishmentsTable.reason],
                    expiry = row[PunishmentDataTable.endTime],
                    appealed = false,
                    id = row[PunishmentsTable.id]
                )
            }
        }
    }

    fun insertPunishment(punishment: Punishment): Punishment {
        transaction(dbConnection) {
            PunishmentsTable.insert {
                it[userId] = punishment.target!!.uniqueId
                it[punishmentTypeId] = punishment.type.ordinal
                it[reason] = punishment.reason
            }
            PunishmentDataTable.insert {
                it[userId] = punishment.target!!.uniqueId
            }
        }
        return punishment
    }

    // fun getPunishments(sortBy: SortBy, page: Int, pageSize: Int, arbiter: Arbiter): List<Punishment> {
    //     val punishments = mutableListOf<Punishment>()
    //     transaction(dbConnection) {
    //         PunishmentsTable.selectAll()
    //             .where { arbiterUniqueId eq arbiter.uniqueId.toString() }
    //             .limit(pageSize, ((page - 1) * pageSize).toLong())
    //             .orderBy(orderBy(sortBy))
    //             .iterator().forEach {
    //                 punishments.add(
    //                     sh.foxboy.bapp.punishment.BappPunishment(
    //                         PunishmentType.fromOrdinal(it[type]), BappArbiter(it[arbiterName], UUID.fromString(it[arbiterUniqueId])), BappUser(it[targetName], UUID.fromString(it[targetUniqueId])), it[reason], Date.from(Instant.ofEpochMilli(it[expiry])), it[appealed], it[punishId]
    //                     )
    //                 )
    //             }
    //     }
    //     return punishments.toList()
    // }

    fun getPunishments(sortBy: SortBy, page: Int, pageSize: Int, user: User): List<Punishment> {
        val punishments = mutableListOf<Punishment>()

        transaction(dbConnection) {
            val query = PunishmentsTable.join(UserTable, JoinType.INNER, onColumn = PunishmentsTable.userId, otherColumn = UserTable.uniqueId) // Join with targetUser
            .join(PunishmentDataTable, JoinType.INNER, onColumn = PunishmentsTable.id, otherColumn = PunishmentDataTable.punishmentTypeId) // Join with punishmentData
            .join(UserTable, JoinType.INNER, onColumn = PunishmentDataTable.issuedBy, otherColumn = UserTable.uniqueId) // Join with issuedByUser
            .join(PunishmentTypeTable, JoinType.INNER, onColumn = PunishmentsTable.punishmentTypeId, otherColumn = PunishmentTypeTable.id) // Join with punishmentType
            .selectAll()
            .where { PunishmentsTable.userId eq user.uniqueId }
            .limit(pageSize, ((page - 1) * pageSize).toLong())
            .orderBy(orderBy(sortBy))

            val iterator = query.iterator()

            while (iterator.hasNext()) {
                val row = iterator.next()
                val punishmentType = PunishmentType.valueOf(row[PunishmentTypeTable.name])

                // Map the arbiter details
                val arbiter = BappArbiter(
                    name = row[UserTable.username],
                    uniqueId = row[PunishmentDataTable.issuedBy]
                )

                // Map the target user details
                val target2 = BappUser(
                    name = row[UserTable.username],
                    uniqueId = row[PunishmentsTable.userId]
                )
                punishments.add(
                    sh.foxboy.bapp.punishment.BappPunishment(
                        type = punishmentType,
                        arbiter = arbiter,
                        target = target2,
                        reason = row[PunishmentsTable.reason],
                        expiry = row[PunishmentDataTable.endTime],
                        appealed = false,
                        id = row[PunishmentsTable.id]
                    )
                )
            }
        }
        return punishments.toList()
    }

    // fun getPunishments(sortBy: SortBy, page: Int, pageSize: Int, user: User): List<Punishment> {
    //     val punishments = mutableListOf<Punishment>()
    //     transaction(dbConnection) {
    //         PunishmentsTable.selectAll()
    //             .where { targetUniqueId eq user.uniqueId.toString() }
    //             .limit(pageSize, ((page - 1) * pageSize).toLong())
    //             .orderBy(orderBy(sortBy))
    //             .iterator().forEach {
    //                 punishments.add(
    //                     sh.foxboy.bapp.punishment.BappPunishment(
    //                         PunishmentType.fromOrdinal(it[type]), BappArbiter(it[arbiterName], UUID.fromString(it[arbiterUniqueId])), BappUser(it[targetName], UUID.fromString(it[targetUniqueId])), it[reason], Date.from(Instant.ofEpochMilli(it[expiry])), it[appealed], it[punishId]
    //                     )
    //                 )
    //             }
    //     }
    //     return punishments.toList()
    // }

    fun getPunishments(sortBy: SortBy, page: Int, pageSize: Int): List<Punishment> {
        val punishments = mutableListOf<Punishment>()
        transaction(dbConnection) {
            val query = PunishmentsTable.join(UserTable, JoinType.INNER, onColumn = PunishmentsTable.userId, otherColumn = UserTable.uniqueId) // Join with targetUser
            .join(PunishmentDataTable, JoinType.INNER, onColumn = PunishmentsTable.id, otherColumn = PunishmentDataTable.punishmentTypeId) // Join with punishmentData
            .join(UserTable, JoinType.INNER, onColumn = PunishmentDataTable.issuedBy, otherColumn = UserTable.uniqueId) // Join with issuedByUser
            .join(PunishmentTypeTable, JoinType.INNER, onColumn = PunishmentsTable.punishmentTypeId, otherColumn = PunishmentTypeTable.id) // Join with punishmentType
            .selectAll()
            .limit(pageSize, ((page - 1) * pageSize).toLong())
            .orderBy(orderBy(sortBy))

            val iterator = query.iterator()

            while (iterator.hasNext()) {
                val row = iterator.next()
                val punishmentType = PunishmentType.valueOf(row[PunishmentTypeTable.name])

                // Map the arbiter details
                val arbiter = BappArbiter(
                    name = row[UserTable.username],
                    uniqueId = row[PunishmentDataTable.issuedBy]
                )

                // Map the target user details
                val target2 = BappUser(
                    name = row[UserTable.username],
                    uniqueId = row[PunishmentsTable.userId]
                )
                punishments.add(
                    sh.foxboy.bapp.punishment.BappPunishment(
                        type = punishmentType,
                        arbiter = arbiter,
                        target = target2,
                        reason = row[PunishmentsTable.reason],
                        expiry = row[PunishmentDataTable.endTime],
                        appealed = false,
                        id = row[PunishmentsTable.id]
                    )
                )
            }
        }
        return punishments.toList()
    }

    private fun orderBy(sortBy: SortBy): (Pair<Expression<*>, SortOrder>) = when (sortBy) {
        SortBy.DATE_ASC -> PunishmentDataTable.startTime to SortOrder.ASC
        SortBy.DATE_DESC -> PunishmentDataTable.startTime to SortOrder.DESC
        SortBy.EXPIRY_ASC -> PunishmentDataTable.endTime to SortOrder.ASC
        SortBy.EXPIRY_DESC -> PunishmentDataTable.endTime to SortOrder.DESC
        SortBy.USERNAME_ASC -> UserTable.username to SortOrder.ASC
        SortBy.USERNAME_DESC -> UserTable.username to SortOrder.DESC
    }

    /**************
     * END PUNISHMENT UTILS
     */
}
