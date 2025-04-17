package me.hugs_me.better_welcome_messages

import me.hugs_me.better_welcome_messages.BetterWelcomeMessages.MOD_ID

object BWMPermissions {
    const val MOD_PERMISSION = MOD_ID
    const val SEE_PERMISSION = "$MOD_ID.see"
    const val HAS_SENT_PERMISSION = "$MOD_ID.has_sent"
    const val EDIT_MESSAGE_PERMISSION = "$MOD_ID.config"
    const val GET_MESSAGE_PERMISSION = "$MOD_ID.receive.message"
    const val SEE_UPDATE_PERMISSION = "$MOD_ID.receive.update"
}