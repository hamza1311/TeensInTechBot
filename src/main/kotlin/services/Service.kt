package services

import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.eq

data class Save(val data: String, val savedBy: Long)

object Service {
    private val client = KMongo.createClient("mongodb://user:pass@127.0.0.1:27017/test").coroutine
    private val database = client.getDatabase("test")
    private val collection = database.getCollection<Save>()

    suspend fun insert(save: Save) {
        collection.insertOne(save)
    }

    suspend fun find(savedBy: Long): List<Save> {
        return collection.find(Save::savedBy eq savedBy).toList()
    }
}