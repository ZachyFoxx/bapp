package sh.foxboy.bapp.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.entity.BappUser

class PlayerConnectionListener : Listener, WithPlugin {

    @EventHandler(priority =  EventPriority.MONITOR)
    fun playerLoginEvent(event: AsyncPlayerPreLoginEvent) {
        if (event.loginResult == AsyncPlayerPreLoginEvent.Result.ALLOWED)
            plugin.userCache.put(BappUser(event.name, event.uniqueId))
    }
}
