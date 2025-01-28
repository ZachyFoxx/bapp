/*
 * Copyright (c) 2021 Zachery Elliot <zachery@foxboy.sh>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player
import sh.foxboy.bapp.api.punishment.PunishmentType
import sh.foxboy.bapp.commands.arguments.PunishmentTypeArgument.punishmentTypeArgument

val debugPunishCommand = CommandAPICommand("punish").withPermission("bapp.punish")
    .withArguments(punishmentTypeArgument("type"))
    .withArguments(PlayerArgument("target"))
    .withArguments(GreedyStringArgument("reason"))
    .executesPlayer(PlayerCommandExecutor { player, args ->
        val type = args[0] as PunishmentType
        val target = args[1] as Player
        val reason = args[2] as String

        // val punishment = Bapp.plugin.punishmentManager.createPunishment(type, BappArbiter(player.name, player.uniqueId), BappUser(target.name, target.uniqueId), reason, Date.from(Instant.now()))

        // val response = punishment.commit()
        // player.sendMessage("Punishment responded with $response")
        player.sendMessage("TODO")
    })
