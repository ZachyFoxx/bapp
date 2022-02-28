package sh.foxboy.bapp.entity

import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.api.entity.Arbiter
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.api.punishment.PunishmentResponse
import sh.foxboy.bapp.api.punishment.PunishmentType
import java.time.Instant
import java.util.Date
import java.util.UUID

class BappUser(private val name: String, private val  uniqueId: UUID) : sh.foxboy.bapp.api.entity.User {

    private val manager = Bapp.plugin.punishmentManager

    override fun getName(): String {
        return this.name
    }

    override fun getUniqueId(): UUID {
        return this.uniqueId
    }

    override fun ban(reason: String, arbiter: Arbiter): PunishmentResponse {
        return manager.createPunishment(PunishmentType.BAN, arbiter,this, reason, Date.from(Instant.MAX)).commit()
    }

    override fun ban(reason: String, arbiter: Arbiter, expiry: Date): PunishmentResponse {
        return manager.createPunishment(PunishmentType.BAN, arbiter,this, reason, expiry).commit()
    }

    override fun mute(reason: String, arbiter: Arbiter): PunishmentResponse {
        return manager.createPunishment(PunishmentType.MUTE, arbiter,this, reason, Date.from(Instant.MAX)).commit()
    }

    override fun mute(reason: String, arbiter: Arbiter, expiry: Date): PunishmentResponse {
        return manager.createPunishment(PunishmentType.MUTE, arbiter,this, reason, expiry).commit()
    }

    override fun warn(reason: String, arbiter: Arbiter): PunishmentResponse {
        return manager.createPunishment(PunishmentType.WARN, arbiter,this, reason, Date.from(Instant.MAX)).commit()
    }

    override fun warn(reason: String, arbiter: Arbiter, expiry: Date): PunishmentResponse {
        return manager.createPunishment(PunishmentType.WARN, arbiter,this, reason, expiry).commit()
    }

    override fun kick(reason: String, arbiter: Arbiter): PunishmentResponse {
        return manager.createPunishment(PunishmentType.KICK, arbiter,this, reason, Date.from(Instant.MAX)).commit()
    }

    override fun getPunishments(): MutableList<Punishment> {
        TODO("Not yet implemented")
    }

    override fun getKey(): String {
        return this.uniqueId.toString()
    }

}
