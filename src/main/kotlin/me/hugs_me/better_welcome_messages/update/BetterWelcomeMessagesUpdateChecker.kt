package me.hugs_me.better_welcome_messages.update

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import me.hugs_me.better_welcome_messages.BetterWelcomeMessages.MOD_ID
import me.hugs_me.better_welcome_messages.Configs
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.Version
import net.minecraft.GameVersion
import net.minecraft.MinecraftVersion
import okhttp3.ConnectionSpec
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import java.security.MessageDigest
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

object BetterWelcomeMessagesUpdateChecker {
    private val moshi = Moshi.Builder().build()
    val mod: ModContainer = FabricLoader.getInstance().getModContainer(MOD_ID).get()
    private val minecraftVersion: GameVersion = MinecraftVersion.CURRENT
    private val updatedVersionFileBodyJsonAdapter = UpdatedVersionFileBodyJsonAdapter(moshi)
    private val updatedVersionResponseBodyJsonAdapter = UpdatedVersionResponseBodyJsonAdapter(moshi)
    private val logger = LoggerFactory.getLogger("better-welcome-messages-update-checker")
    private var backingUpdate: UpdatedVersionResponseBody? = null
    var update: UpdatedVersionResponseBody?
        set(value) = updateLock.write { backingUpdate = value }
        get() = updateLock.read { backingUpdate?.copy() }
    private val updateLock = ReentrantReadWriteLock()

    private val client = OkHttpClient.Builder()
        .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
        .build()

    fun check() = runBlocking {
        if (Configs.mainConfig.checkForUpdates.get()) {
            update = iCheck()
        }
        return@runBlocking update
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun iCheck(): UpdatedVersionResponseBody? {
        logger.info("Checking for updates...")

        val me = mod.origin.paths.first().toFile()
        if (me.extension != ".jar") {
            logger.warn("$me is not a jar file! cannot check for updates!")
            return null
        }
        val digest = MessageDigest.getInstance("SHA-512")
        digest.update(me.readBytes())
        val textDigest = digest.digest().toHexString()
        val json = updatedVersionFileBodyJsonAdapter.toJson(
            UpdatedVersionFileBody(
                setOf("fabric"),
                setOf(minecraftVersion.name)
            )
        )
        val requestBody = json.toRequestBody("application/json".toMediaType())

        /**
         * see [docs](https://docs.modrinth.com/api/operations/getlatestversionfromhash/)
         */
        val url = HttpUrl.Builder().host("api.modrinth.com").scheme("https")
            .addEncodedPathSegments("v2/version_file/")
            .addPathSegment(textDigest)
            .addEncodedPathSegment("update")
            .addEncodedQueryParameter("algorythm", "sha512")
            .build()

        val request = Request.Builder().url(url)
            .header("User-Agent", "NinekoTheCat/BetterWelcomeMessages/${mod.metadata.version} (cnotsomark@gmail.com)")
            .post(requestBody).build()
        val response = client.newCall(request).execute()
        val body = response.body
        logger.debug("got response for update check: {}", response)
        if (!response.isSuccessful || body == null) {
            logger.warn("could not find a new version because of $response")
            return null
        }
        val updatedVersionResponseBody = updatedVersionResponseBodyJsonAdapter.fromJson(body.source()) ?: return null
        if (updatedVersionResponseBody.isUpdateAvailable()) {
            logger.info("Found a new version of better welcome messages! ${updatedVersionResponseBody.parsedVersionNumber} from ${mod.metadata.version}")
        }
        logger.info("Successfully completed update check!")
        return updatedVersionResponseBody
    }
}

@JsonClass(generateAdapter = true)
data class UpdatedVersionFileBody(val loaders: Set<String>, @Json(name = "game_versions") val gameVersions: Set<String>)

@JsonClass(generateAdapter = true)

data class UpdatedVersionResponseBody(
    val name: String,
    @Json(name = "version_number")
    val versionNumber: String,
    val changelog: String,
    val files: Collection<UpdateVersionFile>
) {
    val parsedVersionNumber: Version = Version.parse(versionNumber)
    fun isUpdateAvailable(): Boolean =
        parsedVersionNumber > BetterWelcomeMessagesUpdateChecker.mod.metadata.version

    fun getDownloadLink(): String = "https://modrinth.com/mod/betterwelcomemessages/version/$versionNumber"
}

@JsonClass(generateAdapter = true)
data class UpdateVersionFile(val primary: Boolean, val url: String)