package dbshit

import java.util.*

data class Ban(val user: Long, val bannedBy: Long, val reason: String)

data class Kick(val user: Long, val kickedBy: Long, val reason: String)

data class Warning(val user: Long, val warnedBy: Long, val reason: String, val id: UUID = UUID.randomUUID())

data class Role(val name: String, val id: Long, val assignedTo: MutableSet<User>) {
    data class User(val username: String, val id: Long)
}