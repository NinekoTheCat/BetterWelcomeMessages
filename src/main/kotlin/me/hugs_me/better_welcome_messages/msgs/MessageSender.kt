package me.hugs_me.better_welcome_messages.msgs

import eu.pb4.placeholders.api.ParserContext
import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.parsers.ParserBuilder
import me.fzzyhmstrs.fzzy_config.util.FcText.isEmpty
import me.hugs_me.better_welcome_messages.BWMPermissions
import me.hugs_me.better_welcome_messages.Configs
import me.hugs_me.better_welcome_messages.MessageSendStrategy
import me.hugs_me.better_welcome_messages.database.Database
import me.hugs_me.better_welcome_messages.database.PlayerData
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.dizitart.kno2.session
import org.dizitart.kno2.tx
import org.dizitart.no2.filters.FluentFilter.where
import org.dizitart.no2.transaction.Transaction
import org.slf4j.LoggerFactory
import java.util.*

@Environment(EnvType.SERVER)
object MessageSender {
    private val listenerParser = ParserBuilder.of().simplifiedTextFormat().globalPlaceholders().build()

    private val logger = LoggerFactory.getLogger("better-welcome-messages-msg-sender")
    fun sendWelcomeMessageToPlayerOnlyIfWeShould(
        player: ServerPlayerEntity,
        message: List<String> = Configs.mainConfig.welcomeMessageText.get()
    ) {
        if (shouldMessageBeSentToPlayer(player, message))
            sendWelcomeMessageToPlayer(player, message)
    }


    fun sendWelcomeMessageToPlayer(
        player: ServerPlayerEntity,
        message: List<String> = Configs.mainConfig.welcomeMessageText.get()
    ) {
        if (message.isEmpty()) {
            return
        }
        logger.debug("sending join msg to {} , {}", player.displayName, player.uuid)
        val context: ParserContext = PlaceholderContext.of(player).asParserContext()
        var accumulator = Text.empty() as Text
        if (message.count() == 1) {
            accumulator = Text.of(listenerParser.parseText(message[0], context))
        } else if (message.count() >= 0) {
            accumulator =
                message.map { listenerParser.parseText(it, context).copyContentOnly() }.reduce { acc, mutableText ->
                    acc.append("\n").append(mutableText)
                }
        }
        if (accumulator.isEmpty()) {
            return
        }
        player.sendMessage(accumulator)
    }

    private fun shouldMessageBeSentToPlayer(
        player: ServerPlayerEntity,
        message: List<String> = Configs.mainConfig.welcomeMessageText.get()
    ): Boolean {
        var shouldSendMessage = false
        Database.db.session {
            tx {
                shouldSendMessage = checkIfMessageShouldBeSent(message, player)
            }
        }
        return shouldSendMessage
    }

    @Environment(EnvType.SERVER)
    fun playersWhichHadBeenSentAMessage(players: Collection<ServerPlayerEntity>): Collection<ServerPlayerEntity> {
        val playerIds = players.map { it.uuid.toString() }.toTypedArray()
        var list = Collections.emptyList<UUID>()
        Database.db.session {
            tx {
                val collection = Database.playerRepository
                list = collection.find(where("uuid").`in`(*playerIds)).map { UUID.fromString(it.uuid) }
                    .toList()
            }
        }
        return players.filter { list.contains(it.uuid) }
    }

    private fun Transaction.checkIfMessageShouldBeSent(
        message: List<String>,
        player: ServerPlayerEntity
    ): Boolean {
        if (!Permissions.check(player, BWMPermissions.GET_MESSAGE_PERMISSION, true))
            return false
        val collection = Database.playerRepository
        val user = collection.find(where("uuid").eq(player.uuid.toString())).firstOrNull()
        val strategy = Configs.mainConfig.strategy.get()
        val h = message.toTypedArray().contentDeepHashCode()
        if (user == null) {
            collection.insert(PlayerData(h, player.uuid.toString()))
            commit()
            return true
        }
        when (strategy) {
            MessageSendStrategy.ONCE -> return false
            MessageSendStrategy.HASH_CHANGED -> {
                if (h == user.hash) {
                    return false
                }
                user.hash = h
                collection.update(user)
                commit()
                return true
            }
        }
    }
}