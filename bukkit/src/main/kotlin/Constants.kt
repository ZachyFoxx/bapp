/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp

object Constants {
    object Permissions {
        const val BAN = "bapp.ban"
        const val MUTE = "bapp.mute"
        const val KICK = "bapp.kick"
        const val WARN = "bapp.warn"
        const val BAN_IMMUNE = "bapp.ban.immune"
        const val MUTE_IMMUNE = "bapp.mute.immune"
        const val KICK_IMMUNE = "bapp.kick.immune"
        const val WARN_IMMUNE = "bapp.warn.immune"
    }

    object SettingsPaths {
        const val DATABASE_HOST = "database.host"
        const val DATABASE_PORT = "database.port"
        const val DATABASE_DATABASE = "database.database"
        const val DATABASE_USERNAME = "database.username"
        const val DATABASE_PASSWORD = "database.password"
        const val DATABASE_TABLE_PREFIX = "database.table-prefix"
        const val DATABASE_MAX_RECONNECTS = "database.max-reconnects"
        const val DATABASE_USE_SSL = "database.use-ssl"
    }
}
