package me.hugs_me.better_welcome_messages.database

import org.dizitart.no2.Nitrite
import org.dizitart.no2.repository.ObjectRepository

object Database {
    lateinit var db: Nitrite
    val playerRepository: ObjectRepository<PlayerData> get() = db.getRepository(PlayerData::class.java, "player-data")
}