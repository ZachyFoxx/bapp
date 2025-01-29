package sh.foxboy.bapp.database.tables

import org.jetbrains.exposed.sql.Table
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants

object AppealStatusesTable : Table(Bapp.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "bapp_") + "appeal_status") {
    val id = integer("id").autoIncrement() // Unique ID for appeal status
    val name = varchar("name", 50) // Name of the appeal status (e.g., "Pending", "Approved", "Denied")
    val description = text("description") // Description of the appeal status

    override val primaryKey = PrimaryKey(id)
}
