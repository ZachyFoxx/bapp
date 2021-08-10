/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.punishment

import java.util.Date
import org.bukkit.OfflinePlayer
import sh.foxboy.bapp.api.punishment.Punishment
import sh.foxboy.bapp.api.punishment.PunishmentResponse
import sh.foxboy.bapp.api.punishment.PunishmentType

class Punishment(private val type: PunishmentType, private val arbiter: OfflinePlayer, private val target: OfflinePlayer?, private var reason: String, private var expiry: Date) : Punishment {

    private var id = 0

    init {
        this.id = 1 // TODO: get last id from database
    }

    // TODO: add to database after doing checks
    override fun commit(): PunishmentResponse {
        return PunishmentResponse.OK
    }

    override fun getId(): Int {
        return this.id
    }

    override fun getType(): PunishmentType {
        return this.type
    }

    override fun getArbiter(): OfflinePlayer {
        return this.arbiter
    }

    override fun getTarget(): OfflinePlayer? {
        return this.target
    }

    override fun getReason(): String {
        return this.reason
    }

    override fun getExpiry(): Date {
        return this.expiry
    }
}
