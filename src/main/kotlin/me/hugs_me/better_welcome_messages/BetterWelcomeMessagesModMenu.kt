package me.hugs_me.better_welcome_messages

import com.terraformersmc.modmenu.api.ModMenuApi
import com.terraformersmc.modmenu.api.UpdateChecker
import me.hugs_me.better_welcome_messages.update.BetterWelcomeMessagesUpdateChecker

object BetterWelcomeMessagesModMenu : ModMenuApi {
    override fun getUpdateChecker(): UpdateChecker {
        return UpdateChecker(BetterWelcomeMessagesUpdateChecker::check)
    }
}