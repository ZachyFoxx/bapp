package sh.foxboy.bapp.commands

import dev.jorel.commandapi.CommandAPICommand
import sh.foxboy.bapp.Bapp

internal val plugin = Bapp.plugin
internal val messageFormatter = plugin.messageFormatter

internal fun commandStub(name: String, permission: String): CommandAPICommand = CommandAPICommand(name).withPermission(permission)

internal enum class PunishmentFlag {
    SILENT,
    GLOBAL,
    LOCAL;

    companion object {
        // Convert a single character string flag into the corresponding PunishmentFlag enum value
        fun fromFlag(flag: String): PunishmentFlag? {
            return when (flag.uppercase()) {
                "S" -> SILENT // "S" maps to PunishmentFlag.SILENT
                "G" -> GLOBAL // "G" maps to PunishmentFlag.GLOBAL
                "L" -> LOCAL // "L" maps to PunishmentFlag.LOCAL
                else -> null // Return null if the flag is not recognized
            }
        }
    }
}

internal fun parseFlags(flagArg: String?): Set<PunishmentFlag> {
    val flags = mutableSetOf<PunishmentFlag>()

    // If flagArg is not null and not empty, process each flag
    flagArg?.let {
        it.uppercase().forEach { char ->
            PunishmentFlag.fromFlag(char.toString())?.let { flag ->
                flags.add(flag) // Add the corresponding PunishmentFlag to the set
            }
        }
    }
    return flags
}
