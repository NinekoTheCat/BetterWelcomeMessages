package me.hugs_me.better_welcome_messages.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.hugs_me.better_welcome_messages.BWMPermissions
import me.hugs_me.better_welcome_messages.msgs.MessageSender
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

/*
Command which queries and retrieves players who received the message
 */
class HasSentCommand : BetterWelcomeMessagesCommand {
    override fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>,
        parent: LiteralArgumentBuilder<ServerCommandSource>
    ) {
        dispatcher.register(
            parent.then(
            CommandManager.literal("has_sent")
            .requires(Permissions.require(BWMPermissions.HAS_SENT_PERMISSION, 2))
            .then(
                CommandManager.argument("players", EntityArgumentType.players())
                    .executes {
                        execute(it.source, EntityArgumentType.getPlayers(it, "players"))
                    }
            )
        ))
    }

    private fun execute(source: ServerCommandSource, players: MutableCollection<ServerPlayerEntity>): Int {
        val playersWithSentMessageList = MessageSender.playersWhichHadBeenSentAMessage(players)
        val playersWithSentMessage = playersWithSentMessageList.map { it.displayName }.iterator()

        source.sendMessage(Text.of("these players have seen a welcome message before:"))
        source.sendFeedback({ playersWithSentMessage.next() }, false)

        return playersWithSentMessageList.size
    }
}