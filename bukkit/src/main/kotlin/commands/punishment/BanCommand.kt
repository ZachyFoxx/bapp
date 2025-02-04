// package sh.foxboy.bapp.commands.punishment

// import dev.jorel.commandapi.arguments.PlayerArgument
// import dev.jorel.commandapi.arguments.StringArgument
// import dev.jorel.commandapi.executors.CommandExecutor
// import org.bukkit.entity.Player
// import sh.foxboy.bapp.commands.commandStub
// import sh.foxboy.bapp.commands.messageFormatter
// import sh.foxboy.bapp.commands.parseFlags
// import sh.foxboy.bapp.commands.detectFlags
// import sh.foxboy.bapp.commands.parseServerScope
// import sh.foxboy.bapp.commands.parseReason

// val banCommand = commandStub("ban", "")
//     .withArguments(
//         PlayerArgument("player"), // Target player
//         StringArgument("input").setOptional(true) // Single input argument to process
//     )
//     .executes(CommandExecutor { sender, args ->
//         val player = args[0] as Player
//         val input = args[1] as? String ?: ""

//         // Parse the input manually to extract the server, reason, flags
//         val serverScope = parseServerScope(input)
//         val flags = parseFlags(input)
//         val stringFlags = detectFlags(input)
//         val reason = parseReason(input, serverScope, stringFlags)

//         // Create placeholders for the messages
//         val placeholders = mutableMapOf<String, String>()
//         placeholders["player"] = player.name
//         placeholders["target"] = player.name
//         placeholders["reason"] = reason
//         placeholders["serverScope"] = serverScope ?: "No server scope provided"

//         // Define conditions dynamically
//         val conditions = mapOf(
//             "silent" to true
//         )

//         // Get the announcement message with conditions evaluated
//         val announcementMessage = messageFormatter.getMessage("ban.announcement", placeholders, conditions)

//         // Process the ban with the parsed flags and server scope
//         // banPlayer(player, reason, serverScope, flags)
//         sender.sendMessage("Player ${player.name} banned with reason: $reason, flags: $flags for server: $serverScope")
//     })
