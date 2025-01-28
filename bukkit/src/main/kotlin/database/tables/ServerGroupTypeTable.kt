package sh.foxboy.bapp.database.tables

import org.jetbrains.exposed.sql.Table
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants

object ServerGroupTypeTable : Table(Bapp.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "bapp_") + "server_group_types") {
    val id = integer("id").autoIncrement() // Unique ID for server group type
    val name = varchar("name", 50) // Name of the server group type (e.g., "Public", "Private")
    val description = text("description") // Description of the server group type

    override val primaryKey = PrimaryKey(id)
}
