package dbshit

import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.Success
import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.eq

data class Save(val data: String, val savedBy: Long)

object Service {
    private val client = KMongo.createClient("mongodb://db:27017").coroutine
    private val database = client.getDatabase("test")
    private val collection = database.getCollection<Save>()
    private val bannedUserCollection = database.getCollection<Ban>()
    private val kickedUserCollection = database.getCollection<Kick>()
    private val warningsCollection = database.getCollection<Warning>()
    private val selfAssignRolesCollection = database.getCollection<Role>()


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

    suspend fun addRole(role: Role): Success {
        return selfAssignRolesCollection.insertOne(role)
    }

    suspend fun getAllSelfAssignRoles(): List<Role> {
        return selfAssignRolesCollection.find().toList()
    }

    suspend fun getSelfAssignRoleByName(roleName: String): Role {
        return selfAssignRolesCollection.find(Role::name eq roleName).toList().firstOrNull()
            ?: error("More than 1 role with same name found")
    }

    suspend fun updateAssignedRole(role: Role): UpdateResult {
        return selfAssignRolesCollection.updateOne(Role::id eq role.id, role)
    }
}