/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.database.tables

import org.jetbrains.exposed.sql.Table
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants

object AppealTable : Table(Bapp.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "bapp_") + "appeals") {
    val id = integer("id").autoIncrement() // Unique ID for appeal
    val punishmentId = integer("punishment_id").references(PunishmentsTable.id) // Foreign key referencing PunishmentsTable
    val userId = uuid("user_id") // Foreign key referencing UsersTable
    val appealStatusId = integer("appeal_status_id").references(AppealStatusesTable.id) // Foreign key referencing AppealStatusTable
    val reason = text("reason") // Reason for the appeal
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() } // Timestamp of when the appeal was created
    val updatedAt = long("updated_at").clientDefault { System.currentTimeMillis() } // Timestamp of when the appeal was last updated

    override val primaryKey = PrimaryKey(id)
}
