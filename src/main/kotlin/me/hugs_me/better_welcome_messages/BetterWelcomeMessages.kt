package me.hugs_me.better_welcome_messages

import com.mojang.brigadier.CommandDispatcher
import me.hugs_me.better_welcome_messages.commands.HasSentCommand
import me.hugs_me.better_welcome_messages.commands.HelpCommand
import me.hugs_me.better_welcome_messages.commands.SeeCommand
import me.hugs_me.better_welcome_messages.update.BetterWelcomeMessagesUpdateChecker
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.slf4j.LoggerFactory


object BetterWelcomeMessages : ModInitializer {
    private val logger = LoggerFactory.getLogger("better-welcome-messages")
    const val MOD_ID = "better-welcome-messages"
    override fun onInitialize() {
        logger.info("Hello <3 my version is ${BetterWelcomeMessagesUpdateChecker.mod.metadata.version}")
        Configs.init()
        // mod menu isn't installed so we have to run it ourselves
        if (FabricLoader.getInstance()
                .getModContainer("modmenu").isEmpty || (FabricLoader.getInstance().environmentType == EnvType.SERVER)
        )
            BetterWelcomeMessagesUpdateChecker.check()
        CommandRegistrationCallback.EVENT.register { dispatcher: CommandDispatcher<ServerCommandSource>,
                                                     _: CommandRegistryAccess,
                                                     environment: CommandManager.RegistrationEnvironment ->
            if (environment.dedicated) {
                val parent = CommandManager.literal("bwm")
                    .requires(Permissions.require(BWMPermissions.MOD_PERMISSION, 1))
                SeeCommand().register(dispatcher, parent)
                HasSentCommand().register(dispatcher, parent)
                HelpCommand().register(dispatcher, parent)
            }
        }
    }


}