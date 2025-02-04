package sh.foxboy.bapp.commands

import dev.jorel.commandapi.CommandAPICommand
import sh.foxboy.bapp.Bapp

internal val plugin = Bapp.plugin
internal val messageFormatter = plugin.messageFormatter

internal fun commandStub(name: String, permission: String): CommandAPICommand = CommandAPICommand(name).withPermission(permission)

internal enum class PunishmentFlag {
    SILENT,
    PUBLIC,
    GLOBAL,
    LOCAL;

    companion object {
        // Convert a single character string flag into the corresponding PunishmentFlag enum value
        fun fromFlag(flag: String): PunishmentFlag? {
            return when (flag.uppercase()) {
                "S" -> SILENT
                "P" -> PUBLIC
                "G" -> GLOBAL
                "L" -> LOCAL
                else -> null
            }
        }
    }
}

// Helper functions to parse the arguments
internal fun detectFlags(input: String): List<String> {
    // Only look for valid flags -S, -G, -P, -L or combinations with case-insensitivity
    val validFlags = setOf("S", "G", "P", "L")
    val regex = Regex("-(?i)([SGPL]+)") // Match -S, -G, -P, -L or combinations (case-insensitive)
    val flags = regex.findAll(input)
        .map { it.groupValues[1] } // Get the flag combinations
        .flatMap { it.toList().map { flag -> "-$flag" } } // Convert combinations into individual flags

    // Ensure the flags are only valid ones
    return flags.filter { it.substring(1).uppercase() in validFlags }.toList()
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

internal fun parseServerScope(input: String): String? {
    // Check for server:<name> format with case-insensitivity
    val regex = Regex("server:([a-zA-Z0-9_-]+)", RegexOption.IGNORE_CASE)
    val matchResult = regex.find(input)
    return matchResult?.groups?.get(1)?.value // Return the server scope if matched
}

internal fun parseReason(input: String, serverScope: String?, flags: List<String>): String {
    // Remove server and flags from the input to extract the reason
    var reason = input

    serverScope?.let {
        reason = reason.replace("server:$it", "") // Remove server scope from input if present
    }

    flags.forEach {
        reason = reason.replace(it, "") // Remove flags from input if present
    }

    return reason.trim() // Return the remaining reason after cleanup
}
