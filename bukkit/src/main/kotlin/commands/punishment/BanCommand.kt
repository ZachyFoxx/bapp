// package sh.foxboy.bapp.commands.punishment

// import dev.jorel.commandapi.arguments.OfflinePlayerArgument
// import dev.jorel.commandapi.arguments.StringArgument
// import dev.jorel.commandapi.executors.CommandExecutor
// import dev.jorel.commandapi.executors.PlayerCommandExecutor
// import org.bukkit.OfflinePlayer
// import org.bukkit.entity.Player
// import sh.foxboy.bapp.Bapp
// import sh.foxboy.bapp.api.entity.Arbiter
// import sh.foxboy.bapp.api.punishment.PunishmentResponse
// import sh.foxboy.bapp.api.punishment.PunishmentType
// import sh.foxboy.bapp.api.punishment.Punishment
// import sh.foxboy.bapp.commands.PunishmentFlag
// import sh.foxboy.bapp.commands.commandStub
// import sh.foxboy.bapp.commands.detectFlags
// import sh.foxboy.bapp.commands.messageFormatter
// import sh.foxboy.bapp.commands.parseFlags
// import sh.foxboy.bapp.commands.parseReason
// import sh.foxboy.bapp.commands.parseServerScope
// import sh.foxboy.bapp.entity.BappArbiter
// import sh.foxboy.bapp.entity.BappUser
// import sh.foxboy.bapp.utils.TimeUtil

// val banCommand = commandStub("ban", "bapp.command.ban")
//     .withArguments(
//         OfflinePlayerArgument("player"), // Target player
//         StringArgument("input").setOptional(true) // Single input argument to process
//     )
//     .executes(CommandExecutor { sender, args ->
//         val player = args[0] as OfflinePlayer
//         val input = args[1] as? String ?: ""

//         // Parse the input manually to extract the server, reason, flags
//         val serverScope = parseServerScope(input)
//         val flags = parseFlags(input)
//         val stringFlags = detectFlags(input)
//         val reason = parseReason(input, serverScope, stringFlags)

//         var arbiter: Arbiter

//         if (sender is PlayerCommandExecutor) {
//             arbiter = BappArbiter((sender as Player).name, (sender as Player).uniqueId)
//         } else {
//             arbiter = Bapp.plugin.punishmentManagerExplicit.consoleArbiter
//         }
//         val target = BappUser(player.name!!, player.uniqueId)

//         val punishment = Bapp.plugin.punishmentManagerExplicit.createPunishment(
//             PunishmentType.BAN,
//             arbiter,
//             target,
//             reason,
//             null
//         )

//         // Create placeholders for the messages
//         val placeholders = mutableMapOf<String, String>().apply {
//             put("punisher", arbiter.name)
//             put("target", player.name!!)
//             put("reason", reason)
//             put("serverScope", serverScope ?: "all")
//             put("punishmentType", punishment.type.toString())
//             put("banDate", TimeUtil.convertTimestampToString(System.currentTimeMillis()))
//             put("punishId", punishment.id.toString())
//         }

//         // Define conditions dynamically
//         val conditions = mapOf(
//             "silent" to flags.contains(PunishmentFlag.SILENT)
//         )

//         val response = punishment.commit()
//         when (response) {
//             PunishmentResponse.OK -> handleOk(conditions, placeholders, punishment)
//             PunishmentResponse.TARGET_NOT_EXIST -> sender.sendMessage("Target does not exist.")
//             PunishmentResponse.PUNISHMENT_ALREADY_PUSHED -> sender.sendMessage("Target already has an active punishment.")
//             PunishmentResponse.PERMISSION_DENIED -> sender.sendMessage("You do not have permission to perform this punishment.")
//             PunishmentResponse.TARGET_IMMUNE -> sender.sendMessage("Target is immune to this punishment.")
//             PunishmentResponse.DURATION_EXCEEDS_PERMISSION -> sender.sendMessage("Requested duration exceeds your permission limits.")
//             else -> sender.sendMessage("An error occurred while processing the punishment.")
//         }

//         // Get the announcement message with conditions evaluated
//         val announcementMessage = messageFormatter.getMessage("ban.announcement", placeholders, conditions)

//         // Process the ban with the parsed flags and server scope
//         // banPlayer(player, reason, serverScope, flags)
//         sender.sendMessage("Player ${player.name} banned with reason: $reason, flags: $flags for server: $serverScope")
//     })

// private fun handleOk(conditions: Map<String, Boolean>, placeholders: MutableMap<String, String>, punishment: Punishment) {
    
// }
