package sh.foxboy.bapp.entity

import entity.User
import org.jetbrains.annotations.NotNull
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.api.punishment.PunishmentResponse
import sh.foxboy.bapp.api.punishment.PunishmentType
import java.util.Date
import java.util.UUID
import sh.foxboy.bapp.punishment.BappPunishment

class BappUser(private val name: String, private val  uniqueId: UUID) : sh.foxboy.bapp.api.entity.User {
    override fun getName(): String {
        return this.name
    }

    override fun getUniqueId(): UUID {
        return this.uniqueId
    }

    override fun ban(reason: String): PunishmentResponse {
        TODO("Not yet implemented")
    }

    override fun ban(reason: String, expiry: Date): PunishmentResponse {
        TODO("Not yet implemented")
    }

    override fun mute(reason: String): PunishmentResponse {
        TODO("Not yet implemented")
    }

    override fun mute(reason: String, expiry: Date): PunishmentResponse {
        TODO("Not yet implemented")
    }

    override fun warn(reason: String): PunishmentResponse {
        TODO("Not yet implemented")
    }

    override fun warn(reason: String, expiry: Date): PunishmentResponse {
        TODO("Not yet implemented")
    }

    override fun kick(reason: String): PunishmentResponse {
        TODO("Not yet implemented")
    }

    override fun getPunishments(): MutableList<Punishment> {
        TODO("Not yet implemented")
    }

    override fun getKey(): String {
        return this.uniqueId.toString()
    }

}
