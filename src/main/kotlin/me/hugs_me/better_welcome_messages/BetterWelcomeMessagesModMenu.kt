package me.hugs_me.better_welcome_messages

import com.terraformersmc.modmenu.api.ModMenuApi
import com.terraformersmc.modmenu.api.UpdateChannel
import com.terraformersmc.modmenu.api.UpdateChecker
import com.terraformersmc.modmenu.api.UpdateInfo
import me.hugs_me.better_welcome_messages.update.BetterWelcomeMessagesUpdateChecker
import me.hugs_me.better_welcome_messages.update.UpdatedVersionResponseBody

object BetterWelcomeMessagesModMenu : ModMenuApi {
    override fun getUpdateChecker(): UpdateChecker = BWMUpdateChecker
}

object BWMUpdateChecker : UpdateChecker {
    override fun checkForUpdates(): UpdateInfo? = BetterWelcomeMessagesUpdateChecker.check()?.toUpdateInfo()

}

fun UpdatedVersionResponseBody.toUpdateInfo() = UpdateInfoWithUpdatedVersionResponseBody(this)
class UpdateInfoWithUpdatedVersionResponseBody(private val b: UpdatedVersionResponseBody) : UpdateInfo {
    override fun isUpdateAvailable(): Boolean = b.isUpdateAvailable()

    override fun getDownloadLink(): String = b.getDownloadLink()

    override fun getUpdateChannel(): UpdateChannel = UpdateChannel.RELEASE
}