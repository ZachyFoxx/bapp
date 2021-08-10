/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import sh.foxboy.bapp.Constants
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.database.tables.PunishmentsTable

object PostgresHandler : WithPlugin {
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

            driverClassName = "com.dumbdogdiner.stickycommands.libs.org.postgresql.Driver"
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
        return dataSource.isRunning || success
    }

    /**************
     * BEGIN PUNISHMENT UTILS
     */

    /**************
     * END PUNISHMENT UTILS
     */
}
