package dbshit

data class Ban(val user: Long, val bannedBy: Long, val reason: String)