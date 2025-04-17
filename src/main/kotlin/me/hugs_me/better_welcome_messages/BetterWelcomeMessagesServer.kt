package me.hugs_me.better_welcome_messages

import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.parsers.TagParser
import kotlinx.io.files.Path
import me.hugs_me.better_welcome_messages.database.Database
import me.hugs_me.better_welcome_messages.msgs.MessageSender
import me.hugs_me.better_welcome_messages.update.BetterWelcomeMessagesUpdateChecker
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import org.dizitart.kno2.nitrite
import org.dizitart.kno2.serialization.KotlinXSerializationMapper
import org.dizitart.no2.common.module.NitriteModule.module
import org.dizitart.no2.index.NitriteTextIndexer
import org.dizitart.no2.index.fulltext.UniversalTextTokenizer
import org.dizitart.no2.mvstore.MVStoreModule


@Environment(EnvType.SERVER)
object BetterWelcomeMessagesServer : DedicatedServerModInitializer {

    @Environment(EnvType.SERVER)
    override fun onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(::serverStarting)
        ServerLifecycleEvents.SERVER_STOPPING.register(::serverStopping)
        ServerPlayConnectionEvents.JOIN.register(::joinListener)
    }

    @Environment(EnvType.SERVER)
    fun joinListener(
        serverPlayNetworkHandler: ServerPlayNetworkHandler,

        @Suppress("unused") _packetSender: PacketSender,
        @Suppress("unused") _server: MinecraftServer
    ) {
        MessageSender.sendWelcomeMessageToPlayerOnlyIfWeShould(serverPlayNetworkHandler.player)
        val player = serverPlayNetworkHandler.player
        val update = BetterWelcomeMessagesUpdateChecker.update
        if (Permissions.check(
                player,
                BWMPermissions.SEE_UPDATE_PERMISSION,
                4
            ) && update != null && update.isUpdateAvailable
        ) {
            player.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, .75f, 1f)
            player.sendMessage(
                TagParser.SIMPLIFIED_TEXT_FORMAT.parseText(
                    "" +
                            "There is a new update for Better Welcome Messages available!\n" +
                            "<underline><aqua><url:'${update.downloadLink}'>click here to go get the updated version!</url></aqua></underline>\n" +
                            "You can disable these messages by turning off update checking or by updating.",
                    PlaceholderContext.of(player).asParserContext()
                )
            )
        }
    }

    @Environment(EnvType.SERVER)
    fun serverStarting(server: MinecraftServer) {

        val storeModule = MVStoreModule.withConfig()
            .filePath(
                Path(
                    FabricLoader.getInstance().configDir.toAbsolutePath().toString(),
                    "better-welcome-messages",
                    "db"
                ).toString()
            )
            .build()
        Database.db = nitrite {
            loadModule(storeModule)
            loadModule(module(KotlinXSerializationMapper()))
            loadModule(module(NitriteTextIndexer(UniversalTextTokenizer())))
        }

    }

    @Environment(EnvType.SERVER)
    fun serverStopping(_server: MinecraftServer) {
        Database.db.close()

    }
}



