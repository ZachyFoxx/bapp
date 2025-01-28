package sh.foxboy.bapp.database.tables

import org.jetbrains.exposed.sql.Table
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants

object ServerGroupsTable : Table(Bapp.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "bapp_") + "server_groups") {
    val id = uuid("id") // Unique identifier for the server group
    val name = varchar("name", 100) // Name of the server group
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() } // Timestamp of creation
    val updatedAt = long("updated_at").clientDefault { System.currentTimeMillis() } // Timestamp of last update

    override val primaryKey = PrimaryKey(id)
}
