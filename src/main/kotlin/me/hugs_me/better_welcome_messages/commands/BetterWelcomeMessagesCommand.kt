package me.hugs_me.better_welcome_messages.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.server.command.ServerCommandSource

interface BetterWelcomeMessagesCommand {
    fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>,
        parent: LiteralArgumentBuilder<ServerCommandSource>
    )
}