package me.hugs_me.better_welcome_messages.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import eu.pb4.placeholders.api.ParserContext
import eu.pb4.placeholders.api.parsers.ParserBuilder
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class HelpCommand : BetterWelcomeMessagesCommand {
    override fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>,
        parent: LiteralArgumentBuilder<ServerCommandSource>
    ) {
        dispatcher.register(parent.executes(::execute).then(CommandManager.literal("help").executes(::execute)))
    }

    private val helpText = ParserBuilder.of().simplifiedTextFormat().build().parseText(
        """
    Welcome to <rb><url:'https://modrinth.com/project/betterwelcomemessages'>Better Welcome Messages!</url></rb> 
    These are the commands you can execute:
    
    Makes you or a player see the welcome message again
    <font:uniform>- bwm see [player]</font>
    
    Shows you what players had received the welcome message at least once before 
    <font:uniform>- bwm has_sent [players]</font>
""".trimIndent(), ParserContext.of()
    )

    fun execute(context: CommandContext<ServerCommandSource>): Int {
        context.source.sendMessage(helpText)
        return 0
    }

}