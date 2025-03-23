package me.hugs_me.better_welcome_messages

enum class MessageSendStrategy {
    /**
     * Sends the message to a joining player once
     */
    ONCE,

    /**
     * Re-sends a message if the welcome message had changed
     */
    HASH_CHANGED
}