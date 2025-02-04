// package sh.foxboy.bapp.commands.punishment

// import dev.jorel.commandapi.arguments.PlayerArgument
// import dev.jorel.commandapi.arguments.StringArgument
// import dev.jorel.commandapi.executors.CommandExecutor
// import org.bukkit.entity.Player
// import sh.foxboy.bapp.commands.commandStub
// import sh.foxboy.bapp.commands.messageFormatter
// import sh.foxboy.bapp.commands.parseFlags

// val banCommand = commandStub("ban", "")
//     .withArguments(
//         PlayerArgument("player"),
//         StringArgument("reason").setOptional(true),
//         StringArgument("flags").setOptional(true) // For flags like -SG, -SL, etc.
//     )
//     .executes(CommandExecutor { sender, args ->
//         val player = args[0] as Player
//         val reason = args[1] as? String ?: "You have been banned!"
//         val flagsRaw = args[2] as? String ?: ""

//         val flags = parseFlags(flagsRaw)

//         val placeholders = mutableMapOf<String, String>()
//         placeholders["player"] = player.name
//         placeholders["target"] = player.name
//         placeholders["reason"] = reason

//         // Define conditions dynamically
//         val conditions = mapOf(
//             "silent" to true,
//             "isBanned" to true // Example of another condition
//         )

//         // Get the announcement message with conditions evaluated
//         val announcementMessage = messageFormatter.getMessage("ban.announcement", *placeholders.values.toTypedArray(), conditions)

//         // Process the ban with the parsed flags
//         // banPlayer(player, reason, banType)
//         // sender.sendMessage("Player ${player.name} banned with reason: $reason and flags: $banType")
//     })
