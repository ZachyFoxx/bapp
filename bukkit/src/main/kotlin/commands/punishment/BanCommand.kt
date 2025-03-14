package sh.foxboy.bapp.commands.punishment

import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.executors.CommandExecutor
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import sh.foxboy.bapp.Bapp
import sh.foxboy.bapp.api.entity.Arbiter
import sh.foxboy.bapp.api.flag.BehaviorFlag
import sh.foxboy.bapp.api.punishment.PunishmentResponse
import sh.foxboy.bapp.api.punishment.PunishmentType
import sh.foxboy.bapp.commands.PunishmentFlag
import sh.foxboy.bapp.commands.commandStub
import sh.foxboy.bapp.commands.detectFlags
import sh.foxboy.bapp.commands.messageFormatter
import sh.foxboy.bapp.commands.parseFlags
import sh.foxboy.bapp.commands.parseReason
import sh.foxboy.bapp.commands.parseServerScope
import sh.foxboy.bapp.entity.BappArbiter
import sh.foxboy.bapp.entity.BappUser
import sh.foxboy.bapp.punishment.BappPunishment
import sh.foxboy.bapp.util.TimeUtil
import sh.foxboy.bapp.util.UserUtil

val banCommand = commandStub("ban", "bapp.command.ban")
    .withArguments(
        OfflinePlayerArgument("player"), // Target player
        GreedyStringArgument("input").setOptional(true) // Single input argument to process
    )
    .executes(CommandExecutor { sender, args ->
        val player = args[0] as OfflinePlayer
        val input = args[1] as? String ?: ""

        // Parse the input manually to extract the server, reason, flags
        val serverScope = parseServerScope(input)
        val stringFlags = detectFlags(input)
        val flags = parseFlags(stringFlags)
        var reason = parseReason(input, serverScope, stringFlags)
        val silent = flags.contains(PunishmentFlag.SILENT)

        if (reason.trim().equals("")) {
            reason = "The Ban Hammer has spoken!"
        }

        var arbiter: Arbiter

        if (sender is Player) {
            arbiter = BappArbiter(sender.name, sender.uniqueId)
        } else {
            arbiter = Bapp.plugin.punishmentManagerExplicit.consoleArbiter
        }

        val apiPlayer = UserUtil.getFromAnySource(player.uniqueId)
        // we should never reach this, but just in case.
        if (apiPlayer == null) {
            sender.sendMessage(messageFormatter.getMessage("general.punishment_errors.target_not_exist",
                    mutableMapOf<String, String>().apply {
                        put("arbiter", arbiter.name)
                        put("target", args[0] as String)
                    }
                )
            )
        }

        val target = BappUser(apiPlayer!!.name, apiPlayer.uniqueId)
        val flg = mutableListOf<BehaviorFlag>()
        if (silent)
            flg.add(BehaviorFlag.SILENT)

        val punishment = BappPunishment(
            PunishmentType.BAN,
            arbiter,
            target,
            reason,
            null,
            false,
            flg
        )

        // Create placeholders for the messages
        // TODO: Move this to the punishment class as a helper function to get all important data from the punishment through a function
        val placeholders = mutableMapOf<String, String>().apply {
            put("arbiter", arbiter.name)
            put("target", apiPlayer.name)
            put("reason", reason)
            put("serverScope", serverScope ?: "all")
            put("punishmentType", punishment.type.toString())
            put("banDate", TimeUtil.convertTimestampToString(System.currentTimeMillis()))
            put("punishId", punishment.id.toString())
            put("duration_relative", TimeUtil.convertTimestampToString(System.currentTimeMillis() + (86400 * 1000L)))
            put("duration", TimeUtil.convertTimestampToString(System.currentTimeMillis() + (86400 * 1000L), false))
            put("start_date_relative", TimeUtil.convertTimestampToString(System.currentTimeMillis()))
            put("start_date", TimeUtil.convertTimestampToString(System.currentTimeMillis()))
        }

        // Define conditions dynamically
        val conditions = mapOf(
            "silent" to flags.contains(PunishmentFlag.SILENT),
            "public" to flags.contains(PunishmentFlag.PUBLIC),
            "global" to flags.contains(PunishmentFlag.GLOBAL),
            "local" to flags.contains(PunishmentFlag.LOCAL),
            "temporary" to false
        )

        val response = punishment.commit()
        when (response) {
            PunishmentResponse.OK -> handleOk(flags, conditions, placeholders, punishment)
            PunishmentResponse.TARGET_NOT_EXIST -> sender.sendMessage(messageFormatter.getMessage("general.punishment_errors.target_not_exist", placeholders, conditions))
            PunishmentResponse.PUNISHMENT_ALREADY_PUSHED -> sender.sendMessage(messageFormatter.getMessage("ban.already_banned", placeholders, conditions))
            PunishmentResponse.PERMISSION_DENIED -> sender.sendMessage(messageFormatter.getMessage("general.no_permission", placeholders, conditions))
            PunishmentResponse.TARGET_IMMUNE -> sender.sendMessage(messageFormatter.getMessage("general.punishment_errors.target_immune", placeholders, conditions))
            PunishmentResponse.DURATION_EXCEEDS_PERMISSION -> sender.sendMessage(messageFormatter.getMessage("general.punishment_errors.time_exceeds_permission", placeholders, conditions))
            else -> sender.sendMessage(messageFormatter.getMessage("general.error", placeholders, conditions))
        }
    })

private fun handleOk(flags: Set<PunishmentFlag>, conditions: Map<String, Boolean>, placeholders: MutableMap<String, String>, punishment: BappPunishment) {
    // Get the announcement message with conditions evaluated
    val announcementMessage = messageFormatter.getMessage("ban.announcement", placeholders, conditions)

    var silent = flags.contains(PunishmentFlag.SILENT)
    var targetPlayer = Bukkit.getOfflinePlayer(punishment.target!!.uniqueId)

    val kickMessage = messageFormatter.getMessage("ban.kick_message", placeholders, conditions)

    if (targetPlayer.isOnline) {
        val reason = Component.text()
        .content(kickMessage)
        .build()
        targetPlayer.player?.kick(reason)
    }

    if ((Bapp.plugin.config.getBoolean("silent_mode") && !flags.contains(PunishmentFlag.PUBLIC)) || silent) {
        silent = true
    }

    punishment.announce(silent, announcementMessage)
}
