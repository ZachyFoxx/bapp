package sh.foxboy.bapp.util

import com.google.gson.JsonParser
import java.io.InputStreamReader
import java.net.URL
import java.util.UUID
import org.bukkit.Bukkit
import sh.foxboy.bapp.WithPlugin
import sh.foxboy.bapp.api.entity.User
import sh.foxboy.bapp.entity.BappUser

object UserUtil : WithPlugin {

    /**
     * Get a user from multiple sources to ensure we get a valid user
     */
    fun getFromAnySource(username: String): User? {
        if (username.equals(""))
            return null

        val bukkitPlayer = Bukkit.getOfflinePlayer(username)
        if (bukkitPlayer != null) return BappUser(bukkitPlayer.name ?: "NO_NAME", bukkitPlayer.uniqueId)

        // check our local cache first
        var cacheUser: User? = null
        for (u in plugin.userCache.all) {
            if (u.getName().equals(username)) {
                cacheUser = u
            }
        }
        if (cacheUser != null) return cacheUser

        // Lets try our database now...
        val dbUser = plugin.postgresHandler.getUser(username)

        if (dbUser != null) return dbUser

        // Now lets try the mojang API
        try {
            val url = URL("https://api.ashcon.app/mojang/v2/user/" + username)
            val jsonResponse = JsonParser().parse(InputStreamReader(url.openStream()))

            val uuid = jsonResponse.getAsJsonObject().get("uuid").toString().replace("\"", "")
            val uName = jsonResponse.getAsJsonObject().get("username").toString().replace("\"", "")

            if (uuid.equals(""))
                return null

            val webUser = BappUser(uName, UUID.fromString(uuid))
            plugin.userCache.put(webUser)
            // debug.print(String.format("Pulled entry for %s from API", user.getName()));
            // LolBans.getPlugin().getUserCache().put(user);
            // debug.print("Cached user " + user.getName());
            return webUser
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getFromAnySource(uniqueId: UUID): User? {
        val bukkitPlayer = Bukkit.getOfflinePlayer(uniqueId)
        if (bukkitPlayer.name != null) return BappUser(bukkitPlayer.name!!, bukkitPlayer.uniqueId)

        // check our local cache first
        var cacheUser: User? = null
        for (u in plugin.userCache.all) {
            if (u.uniqueId.equals(uniqueId)) {
                cacheUser = u
            }
        }
        if (cacheUser != null) return cacheUser

        // Lets try our database now...
        val dbUser = plugin.postgresHandler.getUser(uniqueId)

        if (dbUser != null) return dbUser

        // Now lets try the mojang API
        try {
            val url = URL("https://api.ashcon.app/mojang/v2/user/" + uniqueId.toString())
            val jsonResponse = JsonParser().parse(InputStreamReader(url.openStream()))

            val uuid = jsonResponse.getAsJsonObject().get("uuid").toString().replace("\"", "")
            val uName = jsonResponse.getAsJsonObject().get("username").toString().replace("\"", "")

            if (uuid.equals(""))
                return null

            val webUser = BappUser(uName, UUID.fromString(uuid))
            plugin.userCache.put(webUser)
            // debug.print(String.format("Pulled entry for %s from API", user.getName()));
            // LolBans.getPlugin().getUserCache().put(user);
            // debug.print("Cached user " + user.getName());
            return webUser
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
