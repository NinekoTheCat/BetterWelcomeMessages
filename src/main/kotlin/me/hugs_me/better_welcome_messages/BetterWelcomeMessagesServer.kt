package me.hugs_me.better_welcome_messages

import kotlinx.io.files.Path
import me.hugs_me.better_welcome_messages.database.Database
import me.hugs_me.better_welcome_messages.msgs.MessageSender
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
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
    ) = MessageSender.sendWelcomeMessageToPlayerOnlyIfWeShould(serverPlayNetworkHandler.player)

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



