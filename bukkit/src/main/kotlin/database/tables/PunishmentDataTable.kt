/*
 * Copyright (c) 2021-2025 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.database.tables

import org.jetbrains.exposed.sql.Table
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants

object PunishmentDataTable : Table(Bapp.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "bapp_") + "punishment_data") {
    val id = integer("id").autoIncrement() // Unique identifier for the punishment
    val punishId = integer("punish_id").references(PunishmentsTable.id)
    val userId = uuid("user_id").references(UserTable.uniqueId) // The user receiving the punishment
    val punishmentTypeId = integer("punishment_type_id").references(PunishmentTypeTable.id) // Type of punishment
    val serverId = uuid("server_id").references(ServersTable.id).nullable() // The server where the punishment occurred
    val reason = text("reason") // Reason for the punishment
    val issuedBy = uuid("issued_by").references(UserTable.uniqueId) // Moderator issuing the punishment
    val startTime = long("start_time") // When the punishment starts
    val endTime = long("end_time").nullable() // When the punishment ends (nullable for indefinite punishments)
    val active = bool("active").default(true) // Whether the punishment is active
    val flags = integer("flags").nullable() // Punishment flags
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() } // Timestamp of creation
    val updatedAt = long("updated_at").clientDefault { System.currentTimeMillis() } // Timestamp of last update

    override val primaryKey = PrimaryKey(id)
}
