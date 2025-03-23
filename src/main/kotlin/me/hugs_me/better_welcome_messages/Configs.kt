package me.hugs_me.better_welcome_messages

import me.fzzyhmstrs.fzzy_config.annotations.RootConfig
import me.fzzyhmstrs.fzzy_config.annotations.WithCustomPerms
import me.fzzyhmstrs.fzzy_config.annotations.WithPerms
import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.api.RegisterType
import me.fzzyhmstrs.fzzy_config.config.Config
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

val ConfigId = Identifier.of(MOD_ID, "main")
val defaultWelcomeText = """
    Welcome to <rb><url:'https://modrinth.com/project/betterwelcomemessages'>Better Welcome Messages!</url></rb> %player:displayname%
    If you are an administrator then don't worry, otherwise change the <font:uniform>main.toml</font> file in <font:uniform>config/better-welcome-messages</font>
    ❤.
    alternatively install it on your client and change it via the config screen.
""".trimIndent().lines()
@RootConfig
class BetterWelcomeMessagesConfig : Config(ConfigId) {
    @WithCustomPerms([BWMPermissions.EDIT_MESSAGE_PERMISSION], 4)
    var welcomeMessageText = ValidatedString("").toList(
        defaultWelcomeText)

    @WithPerms(4)
    var strategy = ValidatedEnum(MessageSendStrategy.HASH_CHANGED, widgetType = ValidatedEnum.WidgetType.CYCLING)
}