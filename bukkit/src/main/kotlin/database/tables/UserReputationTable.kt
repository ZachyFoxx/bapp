package sh.foxboy.bapp.database.tables

import org.jetbrains.exposed.sql.Table
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.Constants

object UserReputationTable : Table(Bapp.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "bapp_") + "user_reputation") {
    val uniqueId = uuid("uuid").references(UserTable.uniqueId)
    val reputationScore = double("reputation_score")
    val reputationFlags = integer("reputation_flags")
    val updatedAt = long("updated_at").clientDefault { System.currentTimeMillis() } // Timestamp of last update

    override val primaryKey = PrimaryKey(uniqueId)
}
