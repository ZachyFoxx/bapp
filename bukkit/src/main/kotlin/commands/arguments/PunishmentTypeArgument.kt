/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.commands.arguments

import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentInfo
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentInfoParser
import dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder
import sh.foxboy.bapp.api.punishment.PunishmentType

object PunishmentTypeArgument {
    fun punishmentTypeArgument(nodeName: String?): Argument? {

        // Construct our CustomArgument that takes in a String input and returns a World object
        return CustomArgument(nodeName, CustomArgumentInfoParser { info: CustomArgumentInfo ->
            // Parse the world from our input
            val type: PunishmentType?
            // what in the...
            try {
                type = PunishmentType.valueOf(info.input())
            } catch (e: IllegalArgumentException) {
                throw CustomArgumentException(MessageBuilder("Unknown punishment type: ").appendArgInput())
            }
            return@CustomArgumentInfoParser type
        }).replaceSuggestions {
            // sure ig....
            return@replaceSuggestions PunishmentType.values().map { it.toString() }.toTypedArray()
        }
    }
}
