/*
 * Copyright (c) 2022 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.api.flag.BehaviorFlag
import sh.foxboy.bapp.api.punishment.PunishmentType
import sh.foxboy.bapp.entity.BappUser
import sh.foxboy.bapp.util.TimeUtil

class PlayerConnectionListener : Listener, WithPlugin {

    @EventHandler(priority = EventPriority.MONITOR)
    fun playerPreLoginEvent(event: AsyncPlayerPreLoginEvent) {
        if (plugin.panic) {
            val reason = Component.text()
            .content("There has been a database error. See console for further detail, or contact server administrator.").color(NamedTextColor.RED)
            .build()
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, reason)
            return
        }

        val punishments = plugin.postgresHandler.getActivePunishments(event.uniqueId, PunishmentType.BAN)

        if (punishments.size > 0) {
            val first = punishments.first()
            val arbiter = first.arbiter
            val target = first.target

            val placeholders = mutableMapOf<String, String>().apply {
                put("arbiter", arbiter.name)
                put("target", target?.name ?: "NO_NAME")
                put("reason", first.reason)
                put("serverScope", "all")
                put("punishmentType", first.type.toString())
                put("banDate", TimeUtil.convertTimestampToString(System.currentTimeMillis()))
                put("punishId", first.id.toString())

                // TEST TEST TEST TEST
                put("start_date_readable", TimeUtil.convertTimestampToString(System.currentTimeMillis()))
                put("duration_readable", TimeUtil.convertTimestampToString(System.currentTimeMillis() + (86400 * 1000L)))
            }

            val flags = first.flags

            // Define conditions dynamically
            val conditions = mapOf(
                "silent" to flags?.contains(BehaviorFlag.SILENT),
                "temporary" to (first.expiry != null)
            )
        }

        if (event.loginResult == AsyncPlayerPreLoginEvent.Result.ALLOWED)
            plugin.userCache.put(BappUser(event.name, event.uniqueId))
    }
}
