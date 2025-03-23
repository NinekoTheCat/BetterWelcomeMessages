package me.hugs_me.better_welcome_messages.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import me.hugs_me.better_welcome_messages.BWMPermissions
import me.hugs_me.better_welcome_messages.msgs.MessageSender
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.EntitySelector
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

/*
Command that lets the player see the current welcome message again
 */
class SeeCommand : BetterWelcomeMessagesCommand {
    override fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>,
        parent: LiteralArgumentBuilder<ServerCommandSource>
    ) {
        dispatcher.register(
            parent.then(
                literal<ServerCommandSource>("see")
                    .requires(Permissions.require(BWMPermissions.SEE_PERMISSION, 2))
                    .then(
                        argument<ServerCommandSource?, EntitySelector?>(
                            "player",
                            EntityArgumentType.players()
                        ).executes {
                            seeCommand(EntityArgumentType.getPlayers(it, "player"))
                        })
                    .requires { it.isExecutedByPlayer }
                    .executes {
                        seeCommand(listOf(it.source.playerOrThrow))
                    }
            ))
    }

    private fun seeCommand(playerEntities: Collection<ServerPlayerEntity>): Int {
        playerEntities.forEach {
            MessageSender.sendWelcomeMessageToPlayer(it)
        }
        return playerEntities.size
    }

}