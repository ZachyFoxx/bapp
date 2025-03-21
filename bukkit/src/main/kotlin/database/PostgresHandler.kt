/*
 * Copyright (c) 2021-2025 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.util.UUID
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.andIfNotNull
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import sh.foxboy.bapp.Constants
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.api.appeal.AppealStatus
import sh.foxboy.bapp.api.entity.Arbiter
import sh.foxboy.bapp.api.entity.User
import sh.foxboy.bapp.api.flag.BehaviorFlag
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.api.punishment.PunishmentType
import sh.foxboy.bapp.api.punishment.SortBy
import sh.foxboy.bapp.database.tables.AppealStatusesTable
import sh.foxboy.bapp.database.tables.AppealTable
import sh.foxboy.bapp.database.tables.BehaviorFlagTypeTable
import sh.foxboy.bapp.database.tables.PunishmentDataTable
import sh.foxboy.bapp.database.tables.PunishmentTypeTable
import sh.foxboy.bapp.database.tables.PunishmentsTable
import sh.foxboy.bapp.database.tables.PunishmentsTable.reason
import sh.foxboy.bapp.database.tables.ServerGroupTypeTable
import sh.foxboy.bapp.database.tables.ServerGroupsTable
import sh.foxboy.bapp.database.tables.UserReputationTable
import sh.foxboy.bapp.database.tables.UserTable
import sh.foxboy.bapp.entity.BappArbiter
import sh.foxboy.bapp.entity.BappUser

class PostgresHandler() : WithPlugin {
    lateinit var dbConnection: Database

    fun init(): Boolean {
        var success: Boolean
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
                SchemaUtils.createMissingTablesAndColumns(
                    AppealStatusesTable,
                    AppealTable,
                    PunishmentDataTable,
                    PunishmentsTable,
                    PunishmentTypeTable,
                    BehaviorFlagTypeTable,
                    ServerGroupsTable,
                    ServerGroupTypeTable,
                    UserTable,
                    UserReputationTable
                )
            } catch (e: Exception) {
                logger.warning("[SQL] Failed to connect to SQL database - invalid connection info/database not up (${e.cause})")
                success = false
            }
        }
        success = true
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
            // Create aliases for the tables using Alias
            val punishmentAlias = Alias(PunishmentsTable, "punishments")
            val userAlias = Alias(UserTable, "user")
            val punishmentDataAlias = Alias(PunishmentDataTable, "punishment_data")
            val punishmentTypeAlias = Alias(PunishmentTypeTable, "punishment_type")
            val issuedByUserAlias = Alias(UserTable, "issued_by_user") // Alias for the arbiter

            val query = punishmentAlias
                .join(userAlias, JoinType.INNER, onColumn = punishmentAlias[PunishmentsTable.userId], otherColumn = userAlias[UserTable.uniqueId]) // Join with targetUser
                .join(punishmentDataAlias, JoinType.INNER, onColumn = punishmentAlias[PunishmentsTable.id], otherColumn = punishmentDataAlias[PunishmentDataTable.punishmentTypeId]) // Join with punishmentData
                .join(punishmentTypeAlias, JoinType.INNER, onColumn = punishmentAlias[PunishmentsTable.punishmentTypeId], otherColumn = punishmentTypeAlias[PunishmentTypeTable.id]) // Join with punishmentType
                .join(issuedByUserAlias, JoinType.INNER, onColumn = punishmentDataAlias[PunishmentDataTable.issuedBy], otherColumn = issuedByUserAlias[UserTable.uniqueId]) // Join with issuedByUser (fixed join condition)
                .selectAll()
                .where { punishmentAlias[PunishmentsTable.id] eq id }
                .firstOrNull()

            // If a row is found, map it to the Punishment object
            query?.let { row ->
                // Get punishment type
                val punishmentType = PunishmentType.fromOrdinal(row[punishmentTypeAlias[PunishmentTypeTable.id]])

                // Map the arbiter details (now using the alias for the arbiter)
                val arbiter = BappArbiter(
                    name = row[issuedByUserAlias[UserTable.username]], // Access arbiter's username by name
                    uniqueId = row[punishmentDataAlias[PunishmentDataTable.issuedBy]] // Access arbiter's UUID by name
                )

                // Map the target user details
                val target = BappUser(
                    name = row[userAlias[UserTable.username]], // Access target user's username by name
                    uniqueId = row[punishmentAlias[PunishmentsTable.userId]] // Access target user's UUID by name
                )

                val appealStatus = getAppealStatus(id)

                val flags: Int? = row[punishmentDataAlias[PunishmentDataTable.flags]]

                // Return the punishment object
                return@transaction sh.foxboy.bapp.punishment.BappPunishment(
                    type = punishmentType,
                    arbiter = arbiter,
                    target = target,
                    reason = row[punishmentAlias[PunishmentsTable.reason]], // Access reason by name
                    expiry = row[punishmentDataAlias[PunishmentDataTable.endTime]], // Access expiry by name
                    appealed = appealStatus == AppealStatus.APPROVED,
                    id = row[punishmentAlias[PunishmentsTable.id]], // Access id by name
                    flags = flags?.let { BehaviorFlag.decodeFlags(it) },
                    date = row[punishmentAlias[PunishmentsTable.createdAt]]
                )
            }
        }
    }

    fun getActivePunishments(target: UUID, type: PunishmentType): List<Punishment> {
        val punishments = mutableListOf<Punishment>()
        transaction(dbConnection) {
            addLogger(StdOutSqlLogger) // This will print SQL statements to the console
            val punishmentAlias = Alias(PunishmentsTable, "punishments")
            val userAlias = Alias(UserTable, "user")
            val punishmentDataAlias = Alias(PunishmentDataTable, "punishment_data")
            val punishmentTypeAlias = Alias(PunishmentTypeTable, "punishment_type")
            val issuedByUserAlias = Alias(UserTable, "issued_by_user") // Alias for the arbiter

            val query = punishmentAlias
                .join(userAlias, JoinType.INNER, onColumn = punishmentAlias[PunishmentsTable.userId], otherColumn = userAlias[UserTable.uniqueId]) // Join with targetUser
                .join(punishmentDataAlias, JoinType.INNER, onColumn = punishmentAlias[PunishmentsTable.id], otherColumn = punishmentDataAlias[PunishmentDataTable.punishId]) // Join with punishmentData
                .join(punishmentTypeAlias, JoinType.INNER, onColumn = punishmentAlias[PunishmentsTable.punishmentTypeId], otherColumn = punishmentTypeAlias[PunishmentTypeTable.id]) // Join with punishmentType
                .join(issuedByUserAlias, JoinType.INNER, onColumn = punishmentDataAlias[PunishmentDataTable.issuedBy], otherColumn = issuedByUserAlias[UserTable.uniqueId]) // Join with issuedByUser (fixed join condition)
                .selectAll()
                .where {
                    (punishmentAlias[PunishmentsTable.userId] eq target)
                        .andIfNotNull { punishmentDataAlias[PunishmentDataTable.active] eq true }
                        .andIfNotNull { punishmentAlias[PunishmentsTable.punishmentTypeId] eq type.ordinal }
                        .andIfNotNull { punishmentDataAlias[PunishmentDataTable.endTime] greater System.currentTimeMillis() or punishmentDataAlias[PunishmentDataTable.endTime].isNull() }
                }

                val iterator = query.iterator()

                while (iterator.hasNext()) {
                    val row = iterator.next()
                    // Get punishment type
                    val punishmentType = PunishmentType.fromOrdinal(row[punishmentTypeAlias[PunishmentTypeTable.id]])

                    // Map the arbiter details (now using the alias for the arbiter)
                    val arbiter = BappArbiter(
                        name = row[issuedByUserAlias[UserTable.username]], // Access arbiter's username by name
                        uniqueId = row[punishmentDataAlias[PunishmentDataTable.issuedBy]] // Access arbiter's UUID by name
                    )

                    // Map the target user details
                    val target2 = BappUser(
                        name = row[userAlias[UserTable.username]], // Access target user's username by name
                        uniqueId = row[punishmentAlias[PunishmentsTable.userId]] // Access target user's UUID by name
                    )

                    val appealStatus = getAppealStatus(row[punishmentAlias[PunishmentsTable.id]])
                    if (appealStatus == AppealStatus.APPROVED) continue

                    val flags: Int? = row[punishmentDataAlias[PunishmentDataTable.flags]]

                    punishments.add(
                        sh.foxboy.bapp.punishment.BappPunishment(
                            type = punishmentType,
                            arbiter = arbiter,
                            target = target2,
                            reason = row[punishmentDataAlias[PunishmentDataTable.reason]], // Access reason by name
                            expiry = row[punishmentDataAlias[PunishmentDataTable.endTime]], // Access expiry by name
                            appealed = appealStatus == AppealStatus.APPROVED,
                            id = row[punishmentAlias[PunishmentsTable.id]], // Access id by name
                            flags = flags?.let { BehaviorFlag.decodeFlags(it) },
                            date = row[punishmentAlias[PunishmentsTable.createdAt]]
                        )
                    )
            }
        }
        return punishments.toList()
    }

    fun getAppealStatus(punishId: Int): AppealStatus {
        return transaction(dbConnection) {
            val query = AppealTable
                .selectAll()
                .where { AppealTable.punishmentId eq punishId }
                .firstOrNull()
            var status: AppealStatus = AppealStatus.NO_APPEAL
            query?.let { row ->
                status = AppealStatus.fromOrdinal(row[AppealTable.appealStatusId])
            }
            return@transaction status
        }
    }

    fun getLastPunishment(target: UUID): Punishment? {
        return transaction(dbConnection) {
            // Create aliases for the tables using Alias
            val punishmentAlias = Alias(PunishmentsTable, "punishments")
            val userAlias = Alias(UserTable, "user")
            val punishmentDataAlias = Alias(PunishmentDataTable, "punishment_data")
            val punishmentTypeAlias = Alias(PunishmentTypeTable, "punishment_type")
            val issuedByUserAlias = Alias(UserTable, "issued_by_user") // Alias for the arbiter

            val query = punishmentAlias
                .join(userAlias, JoinType.INNER, onColumn = punishmentAlias[PunishmentsTable.userId], otherColumn = userAlias[UserTable.uniqueId]) // Join with targetUser
                .join(punishmentDataAlias, JoinType.INNER, onColumn = punishmentAlias[PunishmentsTable.id], otherColumn = punishmentDataAlias[PunishmentDataTable.punishmentTypeId]) // Join with punishmentData
                .join(punishmentTypeAlias, JoinType.INNER, onColumn = punishmentAlias[PunishmentsTable.punishmentTypeId], otherColumn = punishmentTypeAlias[PunishmentTypeTable.id]) // Join with punishmentType
                .join(issuedByUserAlias, JoinType.INNER, onColumn = punishmentDataAlias[PunishmentDataTable.issuedBy], otherColumn = issuedByUserAlias[UserTable.uniqueId]) // Join with issuedByUser (fixed join condition)
                .selectAll()
                .where { PunishmentsTable.userId eq target }
                .firstOrNull()

            // If a row is found, map it to the Punishment object
            query?.let { row ->
                // Get punishment type
                val punishmentType = PunishmentType.fromOrdinal(row[punishmentTypeAlias[PunishmentTypeTable.id]])

                // Map the arbiter details (now using the alias for the arbiter)
                val arbiter = BappArbiter(
                    name = row[issuedByUserAlias[UserTable.username]], // Access arbiter's username by name
                    uniqueId = row[punishmentDataAlias[PunishmentDataTable.issuedBy]] // Access arbiter's UUID by name
                )

                // Map the target user details
                val target2 = BappUser(
                    name = row[userAlias[UserTable.username]], // Access target user's username by name
                    uniqueId = row[punishmentAlias[PunishmentsTable.userId]] // Access target user's UUID by name
                )

                val appealStatus = getAppealStatus(row[punishmentAlias[PunishmentsTable.id]])
                val flags: Int? = row[punishmentDataAlias[PunishmentDataTable.flags]]

                // Return the punishment object
                return@transaction sh.foxboy.bapp.punishment.BappPunishment(
                    type = punishmentType,
                    arbiter = arbiter,
                    target = target2,
                    reason = row[punishmentAlias[PunishmentsTable.reason]], // Access reason by name
                    expiry = row[punishmentDataAlias[PunishmentDataTable.endTime]], // Access expiry by name
                    appealed = appealStatus == AppealStatus.APPROVED,
                    id = row[punishmentAlias[PunishmentsTable.id]], // Access id by name
                    flags = flags?.let { BehaviorFlag.decodeFlags(it) },
                    date = row[punishmentAlias[PunishmentsTable.createdAt]]
                )
            }
        }
    }

    fun checkInsertUsers(punishment: Punishment) {
        transaction(dbConnection) {
            val existingUser = UserTable
            .selectAll()
            .where { UserTable.uniqueId eq punishment.target!!.uniqueId }
            .firstOrNull()

            // If user does not exist, insert them
            if (existingUser == null) {
                UserTable.insert {
                    it[uniqueId] = punishment.target!!.uniqueId
                    it[username] = punishment.target!!.name
                }
            }

            val existingArbiter = UserTable
            .selectAll()
            .where { UserTable.uniqueId eq punishment.arbiter.uniqueId }
            .firstOrNull()

            // If user does not exist, insert them
            if (existingArbiter == null) {
                UserTable.insert {
                    it[uniqueId] = punishment.arbiter.uniqueId
                    it[username] = punishment.arbiter.name
                }
            }
        }
    }

    fun insertPunishment(punishment: Punishment): Punishment {
        transaction(dbConnection) {
            addLogger(StdOutSqlLogger) // This will print SQL statements to the console
            // Check if user exists in bapp_users
            checkInsertUsers(punishment)

            val punishmentId = PunishmentsTable.insert {
                it[userId] = punishment.target!!.uniqueId
                it[punishmentTypeId] = punishment.type.ordinal
                it[reason] = punishment.reason
            }.resultedValues!!.first()[PunishmentsTable.id]

            PunishmentDataTable.insert {
                it[userId] = punishment.target!!.uniqueId
                it[punishId] = punishmentId
                it[reason] = punishment.reason
                it[punishmentTypeId] = punishment.type.ordinal
                it[issuedBy] = punishment.arbiter.uniqueId
                it[startTime] = System.currentTimeMillis()
                it[endTime] = punishment.expiry
                it[active] = true
                it[flags] = punishment.flags?.let { BehaviorFlag.encodeFlags(it) }
            }
        }
        return punishment
    }

    fun getPunishments(sortBy: SortBy, page: Int, pageSize: Int, arbiter: Arbiter): List<Punishment> {
        val punishments = mutableListOf<Punishment>()
        transaction(dbConnection) {
            // Create aliases for the tables using Alias
            val punishmentAlias = Alias(PunishmentsTable, "punishments")
            val userAlias = Alias(UserTable, "user")
            val punishmentDataAlias = Alias(PunishmentDataTable, "punishment_data")
            val punishmentTypeAlias = Alias(PunishmentTypeTable, "punishment_type")
            val issuedByUserAlias = Alias(UserTable, "issued_by_user") // Alias for the arbiter

            val query = punishmentAlias
                .join(userAlias, JoinType.INNER, onColumn = punishmentAlias[PunishmentsTable.userId], otherColumn = userAlias[UserTable.uniqueId]) // Join with targetUser
                .join(punishmentDataAlias, JoinType.INNER, onColumn = punishmentAlias[PunishmentsTable.id], otherColumn = punishmentDataAlias[PunishmentDataTable.punishmentTypeId]) // Join with punishmentData
                .join(punishmentTypeAlias, JoinType.INNER, onColumn = punishmentAlias[PunishmentsTable.punishmentTypeId], otherColumn = punishmentTypeAlias[PunishmentTypeTable.id]) // Join with punishmentType
                .join(issuedByUserAlias, JoinType.INNER, onColumn = punishmentDataAlias[PunishmentDataTable.issuedBy], otherColumn = issuedByUserAlias[UserTable.uniqueId]) // Join with issuedByUser (fixed join condition)
                .selectAll()
                .where { punishmentDataAlias[PunishmentDataTable.issuedBy] eq arbiter.uniqueId }
                .orderBy(orderBy(sortBy))
                .offset(((page - 1) * pageSize).toLong())
                .orderBy(orderBy(sortBy))

                val iterator = query.iterator()

                while (iterator.hasNext()) {
                    val row = iterator.next()
                    val punishmentType = PunishmentType.valueOf(row[PunishmentTypeTable.name])

                    // Map the arbiter details
                    val arbiter2 = BappArbiter(
                        name = row[UserTable.username],
                        uniqueId = row[PunishmentDataTable.issuedBy]
                    )

                    // Map the target user details
                    val target2 = BappUser(
                        name = row[UserTable.username],
                        uniqueId = row[PunishmentsTable.userId]
                    )

                    val appealStatus = getAppealStatus(row[PunishmentsTable.id])
                    val flags: Int? = row[punishmentDataAlias[PunishmentDataTable.flags]]

                    // Return the punishment object
                    return@transaction sh.foxboy.bapp.punishment.BappPunishment(
                        type = punishmentType,
                        arbiter = arbiter2,
                        target = target2,
                        reason = row[punishmentAlias[PunishmentsTable.reason]], // Access reason by name
                        expiry = row[punishmentDataAlias[PunishmentDataTable.endTime]], // Access expiry by name
                        appealed = appealStatus == AppealStatus.APPROVED,
                        id = row[punishmentAlias[PunishmentsTable.id]], // Access id by name
                        flags = flags?.let { BehaviorFlag.decodeFlags(it) },
                        date = row[punishmentAlias[PunishmentsTable.createdAt]]
                    )
                }
            }
            return punishments.toList()
    }

    fun getPunishments(sortBy: SortBy, page: Int, pageSize: Int, user: User): List<Punishment> {
        val punishments = mutableListOf<Punishment>()

        transaction(dbConnection) {
            val query = PunishmentsTable.join(UserTable, JoinType.INNER, onColumn = PunishmentsTable.userId, otherColumn = UserTable.uniqueId) // Join with targetUser
            .join(PunishmentDataTable, JoinType.INNER, onColumn = PunishmentsTable.id, otherColumn = PunishmentDataTable.punishmentTypeId) // Join with punishmentData
            .join(UserTable, JoinType.INNER, onColumn = PunishmentDataTable.issuedBy, otherColumn = UserTable.uniqueId) // Join with issuedByUser
            .join(PunishmentTypeTable, JoinType.INNER, onColumn = PunishmentsTable.punishmentTypeId, otherColumn = PunishmentTypeTable.id) // Join with punishmentType
            .selectAll()
            .where { PunishmentsTable.userId eq user.uniqueId }
            .limit(pageSize)
            .offset(((page - 1) * pageSize).toLong())
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

                val appealStatus = getAppealStatus(row[PunishmentsTable.id])

                val flags: Int? = row[PunishmentDataTable.flags]

                // Return the punishment object
                return@transaction sh.foxboy.bapp.punishment.BappPunishment(
                    type = punishmentType,
                    arbiter = arbiter,
                    target = target2,
                    reason = row[PunishmentsTable.reason], // Access reason by name
                    expiry = row[PunishmentDataTable.endTime], // Access expiry by name
                    appealed = appealStatus == AppealStatus.APPROVED,
                    id = row[PunishmentsTable.id], // Access id by name
                    flags = flags?.let { BehaviorFlag.decodeFlags(it) },
                    date = row[PunishmentsTable.createdAt]
                )
            }
        }
        return punishments.toList()
    }

    fun getUser(username: String): User? {
        return transaction(dbConnection) {
            val query = UserTable.selectAll()
                .where { UserTable.username eq username }
                .firstOrNull()

                query?.let { row ->
                    // Map the arbiter details (now using the alias for the arbiter)
                    val user = BappUser(
                        name = row[UserTable.username],
                        uniqueId = row[UserTable.uniqueId]
                    )
                    // Return the punishment object
                    return@transaction user
                }
        }
    }

    fun getUser(uniqueId: UUID): User? {
        return transaction(dbConnection) {
            val query = UserTable.selectAll()
                .where { UserTable.uniqueId eq uniqueId }
                .firstOrNull()

                query?.let { row ->
                    // Map the arbiter details (now using the alias for the arbiter)
                    val user = BappUser(
                        name = row[UserTable.username],
                        uniqueId = row[UserTable.uniqueId]
                    )
                    // Return the punishment object
                    return@transaction user
                }
        }
    }

    fun getPunishments(sortBy: SortBy, page: Int, pageSize: Int): List<Punishment> {
        val punishments = mutableListOf<Punishment>()
        transaction(dbConnection) {
            val query = PunishmentsTable.join(UserTable, JoinType.INNER, onColumn = PunishmentsTable.userId, otherColumn = UserTable.uniqueId) // Join with targetUser
            .join(PunishmentDataTable, JoinType.INNER, onColumn = PunishmentsTable.id, otherColumn = PunishmentDataTable.punishmentTypeId) // Join with punishmentData
            .join(UserTable, JoinType.INNER, onColumn = PunishmentDataTable.issuedBy, otherColumn = UserTable.uniqueId) // Join with issuedByUser
            .join(PunishmentTypeTable, JoinType.INNER, onColumn = PunishmentsTable.punishmentTypeId, otherColumn = PunishmentTypeTable.id) // Join with punishmentType
            .selectAll()
            .limit(pageSize)
            .offset(((page - 1) * pageSize).toLong())
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

                val appealStatus = getAppealStatus(row[PunishmentsTable.id])

                val flags: Int? = row[PunishmentDataTable.flags]

                // Return the punishment object
                return@transaction sh.foxboy.bapp.punishment.BappPunishment(
                    type = punishmentType,
                    arbiter = arbiter,
                    target = target2,
                    reason = row[PunishmentsTable.reason], // Access reason by name
                    expiry = row[PunishmentDataTable.endTime], // Access expiry by name
                    appealed = appealStatus == AppealStatus.APPROVED,
                    id = row[PunishmentsTable.id], // Access id by name
                    flags = flags?.let { BehaviorFlag.decodeFlags(it) },
                    date = row[PunishmentsTable.createdAt]
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
