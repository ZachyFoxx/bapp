package sh.foxboy.bapp.database.tables

import org.jetbrains.exposed.sql.Table
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants

object ServersTable : Table(Bapp.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "bapp_") + "servers") {
    val id = uuid("id") // Unique identifier for the server
    val name = varchar("name", 100) // Display name of the server
    val serverGroupId = uuid("server_group_id").references(ServerGroupsTable.id).nullable() // Server group (nullable if not part of a group)
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() } // Timestamp of creation
    val updatedAt = long("updated_at").clientDefault { System.currentTimeMillis() } // Timestamp of last update

    override val primaryKey = PrimaryKey(id)
}
