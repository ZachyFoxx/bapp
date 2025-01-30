package sh.foxboy.bapp.database.tables

import org.jetbrains.exposed.sql.Table
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants

object ReputationFlagTypeTable : Table(Bapp.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "bapp_") + "reputation_flag_types") {
    val id = integer("id").autoIncrement() // Unique ID for reputation flag type
    val name = varchar("name", 50) // Name of the flag (e.g., "Cheating", "Toxicity", "Griefing")
    val description = text("description") // Description of the flag type
    val weight = double("weight")

    override val primaryKey = PrimaryKey(id)
}
