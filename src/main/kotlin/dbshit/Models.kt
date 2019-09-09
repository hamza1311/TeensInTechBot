package dbshit

data class Ban(val user: Long, val bannedBy: Long, val reason: String)

data class Kick(val user: Long, val kickedBy: Long, val reason: String)

data class Warning(val user: Long, val warnedBy: Long, val reason: String)