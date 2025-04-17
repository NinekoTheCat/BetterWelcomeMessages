package me.hugs_me.better_welcome_messages

import me.fzzyhmstrs.fzzy_config.annotations.Comment
import me.fzzyhmstrs.fzzy_config.annotations.RootConfig
import me.fzzyhmstrs.fzzy_config.annotations.WithCustomPerms
import me.fzzyhmstrs.fzzy_config.annotations.WithPerms
import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString
import me.hugs_me.better_welcome_messages.BetterWelcomeMessages.MOD_ID
import net.minecraft.util.Identifier

object Configs {
    lateinit var mainConfig: BetterWelcomeMessagesConfig
    fun init() {
        mainConfig = ConfigApi.registerAndLoadConfig(::BetterWelcomeMessagesConfig, RegisterType.BOTH)
    }
}

val ConfigId: Identifier = Identifier.of(MOD_ID, "main")
val defaultWelcomeText = BetterWelcomeMessages::class.java
    .getResource("/assets/better-welcome-messages/default_welcome_message.txt")
    ?.readText()
    ?.trimIndent()
    ?.lines()!!
@RootConfig
class BetterWelcomeMessagesConfig : Config(ConfigId) {
    @Comment("Please see https://ninekothecat.github.io/BetterWelcomeMessages/latest/config_types/file/ for help.")
    @WithCustomPerms([BWMPermissions.EDIT_MESSAGE_PERMISSION], 4)
    var welcomeMessageText = ValidatedString("").toList(
        defaultWelcomeText)

    @WithPerms(4)
    var strategy = ValidatedEnum(MessageSendStrategy.HASH_CHANGED, widgetType = ValidatedEnum.WidgetType.CYCLING)

    @WithPerms(4)
    var checkForUpdates = ValidatedBoolean(true)
}