/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.database.tables

import org.jetbrains.exposed.sql.Table
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants

object PunishmentsTable : Table(Bapp.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "bapp_") + "punishments") {
    val id = integer("id").autoIncrement() // Unique ID for punishment
    val userId = uuid("user_id") // Foreign key referencing UsersTable
    val punishmentTypeId = integer("punishment_type_id") // Foreign key referencing PunishmentTypesTable
    val reason = text("reason") // Reason for the punishment
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() } // Timestamp of when the punishment was created
    val updatedAt = long("updated_at").clientDefault { System.currentTimeMillis() } // Timestamp of when the punishment was last updated

    override val primaryKey = PrimaryKey(id)
}
