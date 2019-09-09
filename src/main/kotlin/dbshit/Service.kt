package dbshit

import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.eq

data class Save(val data: String, val savedBy: Long)

object Service {
    private val client = KMongo.createClient("mongodb://user:pass@127.0.0.1:27017/test").coroutine
    private val database = client.getDatabase("test")
    private val collection = database.getCollection<Save>()
    private val bannedUserCollection = database.getCollection<Ban>()
    private val kickedUserCollection = database.getCollection<Kick>()
    private val warningsCollection = database.getCollection<Warning>()


    suspend fun insert(save: Save) {
        collection.insertOne(save)
    }

    suspend fun find(savedBy: Long): List<Save> {
        return collection.find(Save::savedBy eq savedBy).toList()
    }

    suspend fun insertBannedUser(banned: Ban) {
        bannedUserCollection.insertOne(banned)
    }

    suspend fun getAllBannedUsers(): List<Ban> {
        return bannedUserCollection.find().toList()
    }

    suspend fun insertKickedUser(banned: Kick) {
        kickedUserCollection.insertOne(banned)
    }

    suspend fun getAllKickedUsers(): List<Kick> {
        return kickedUserCollection.find().toList()
    }

    suspend fun warnUser(warn: Warning) {
        warningsCollection.insertOne(warn)
    }

    suspend fun getWarningForUser(user: Long): List<Warning> {
        return warningsCollection.find(Warning::user eq user).toList()
    }
}