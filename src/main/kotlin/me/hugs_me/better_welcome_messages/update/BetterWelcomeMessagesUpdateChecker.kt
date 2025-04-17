package me.hugs_me.better_welcome_messages.update

import com.terraformersmc.modmenu.api.UpdateChannel
import com.terraformersmc.modmenu.api.UpdateInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import kotlinx.serialization.json.JsonNamingStrategy
import me.hugs_me.better_welcome_messages.BetterWelcomeMessages.MOD_ID
import me.hugs_me.better_welcome_messages.Configs
import me.hugs_me.better_welcome_messages.update.BetterWelcomeMessagesUpdateChecker.mod
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.Version
import net.minecraft.GameVersion
import net.minecraft.MinecraftVersion
import org.slf4j.LoggerFactory
import java.security.MessageDigest
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

object BetterWelcomeMessagesUpdateChecker {
    val mod: ModContainer = FabricLoader.getInstance().getModContainer(MOD_ID).get()
    private val minecraftVersion: GameVersion = MinecraftVersion.CURRENT
    private val logger = LoggerFactory.getLogger("better-welcome-messages-update-checker")
    private var backingUpdate: UpdatedVersionResponseBody? = null
    var update: UpdatedVersionResponseBody?
        set(value) = updateLock.write { backingUpdate = value }
        get() = updateLock.read { backingUpdate?.copy() }
    private val updateLock = ReentrantReadWriteLock()

    @OptIn(ExperimentalSerializationApi::class)
    val client = HttpClient(Java) {
        install(Resources)
        install(Logging) {
            this.level = LogLevel.HEADERS
            this.logger = logger

        }
        install(UserAgent) {
            agent = "NinekoTheCat/BetterWelcomeMessages/${mod.metadata.version} (cnotsomark@gmail.com)"
        }
        install(ContentNegotiation) {
            json(Json {
                namingStrategy = JsonNamingStrategy.SnakeCase
            })
        }
    }

    fun check() = runBlocking {
        if (Configs.mainConfig.checkForUpdates.get()) {
            update = asyncCheck()
        }
        return@runBlocking update
    }

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun asyncCheck(): UpdatedVersionResponseBody? {
        logger.info("Checking for updates...")
        val me = mod.origin.paths.first().toFile()
        if (me.extension != ".jar") {
            logger.warn("$me is not a jar file! cannot check for updates!")
            return null
        }
        val digest = MessageDigest.getInstance("SHA-512")
        digest.update(me.readBytes())
        val textDigest = digest.digest().toHexString()
        val request = UpdatedVersionFile(hash = textDigest)
        val response = client.post(request) {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.modrinth.com"
                parameter("algorythm", "sha512")
            }
            contentType(ContentType.Application.Json)
            setBody(UpdatedVersionFileBody(setOf("fabric"), setOf(minecraftVersion.name)))
        }
        logger.debug("got response for update check: {}", response)
        if (response.status != HttpStatusCode.OK) {
            logger.warn("could not find a new version because of $response")
            return null
        }
        val updatedVersionResponseBody = response.body<UpdatedVersionResponseBody>()
        if (updatedVersionResponseBody.isUpdateAvailable) {
            logger.info("Found a new version of better welcome messages! ${updatedVersionResponseBody.parsedVersionNumber} from ${mod.metadata.version}")
        }
        logger.info("Successfully completed update check!")
        return updatedVersionResponseBody
    }
}

/**
 * see [docs](https://docs.modrinth.com/api/operations/getlatestversionfromhash/)
 */

@Resource("/v2/version_file/{hash}/update")
class UpdatedVersionFile(@Suppress("UNUSED") val hash: String)

@Serializable
data class UpdatedVersionFileBody(val loaders: Set<String>, val gameVersions: Set<String>)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class UpdatedVersionResponseBody(
    val name: String,
    private val versionNumber: String,
    val changelog: String,
    val files: Collection<UpdateVersionFile>
) : UpdateInfo {
    val parsedVersionNumber: Version = Version.parse(versionNumber)
    override fun isUpdateAvailable(): Boolean = parsedVersionNumber > mod.metadata.version

    override fun getDownloadLink(): String = "https://modrinth.com/mod/betterwelcomemessages/version/$versionNumber"

    override fun getUpdateChannel(): UpdateChannel = UpdateChannel.RELEASE
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class UpdateVersionFile(val primary: Boolean, val url: String)