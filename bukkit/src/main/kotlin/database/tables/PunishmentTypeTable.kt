package sh.foxboy.bapp.database.tables

import org.jetbrains.exposed.sql.Table
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants

object PunishmentTypeTable : Table(Bapp.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "bapp_") + "punishment_types") {
    val id = integer("id").autoIncrement() // Unique ID for punishment type
    val name = varchar("name", 50) // Name of the punishment type (e.g., "Warn", "Mute", "Ban")
    val description = text("description") // Description of the punishment type

    override val primaryKey = PrimaryKey(id)
}
