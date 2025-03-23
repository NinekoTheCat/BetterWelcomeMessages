package me.hugs_me.better_welcome_messages.database

import kotlinx.serialization.Serializable
import org.dizitart.no2.repository.annotations.Entity
import org.dizitart.no2.repository.annotations.Id
import kotlin.uuid.ExperimentalUuidApi

@Entity
@Serializable
data class PlayerData @OptIn(ExperimentalUuidApi::class) constructor(
    var hash: Int,
    @Id
    var uuid: String
)

