package sh.foxboy.bapp.database.tables

import org.jetbrains.exposed.sql.Table
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants

object UserTable : Table(Bapp.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "bapp_") + "users") {
    val username = varchar("username", 100) // Username of the user
    val uniqueId = uuid("uuid") // Their minecraft UUID

    override val primaryKey = PrimaryKey(uniqueId)
}
