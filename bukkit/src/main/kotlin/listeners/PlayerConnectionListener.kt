/*
 * Copyright (c) 2022 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.entity.BappUser

class PlayerConnectionListener : Listener, WithPlugin {

    @EventHandler(priority = EventPriority.MONITOR)
    fun playerPreLoginEvent(event: AsyncPlayerPreLoginEvent) {
        if (event.loginResult == AsyncPlayerPreLoginEvent.Result.ALLOWED)
            plugin.userCache.put(BappUser(event.name, event.uniqueId))
    }
}
