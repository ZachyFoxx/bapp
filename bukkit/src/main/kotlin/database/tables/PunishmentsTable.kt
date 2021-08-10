/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.database.tables

import org.jetbrains.exposed.sql.Table
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants

object PunishmentsTable : Table(Bapp.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX) ?: "bapp_") {
    val id = integer("id").autoIncrement()

    var type = integer("type")

    var punishedAt = long("punished_at").clientDefault { System.currentTimeMillis() }

    var arbiterUniqueId = varchar("arbiter_uuid", 36)

    var targetUniqueId = varchar("target_uuid", 36)

    var reason = text("reason")

    var expiry = long("expiry")

    var appealed = bool("appealed").default(false)

    override val primaryKey = PrimaryKey(id)
}
