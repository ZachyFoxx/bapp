package sh.foxboy.bapp.entity

import sh.foxboy.bapp.api.entity.Arbiter
import java.util.UUID


class BappArbiter(private val name: String, private val uniqueId: UUID) : Arbiter {

    override fun getName(): String {
        return this.name
    }

    override fun getUniqueId(): UUID {
        return this.uniqueId
    }
}