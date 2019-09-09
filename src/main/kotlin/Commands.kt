import dbshit.*
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.*
import java.awt.Color

fun ping(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    Thread.sleep(1000)
    message.reply("Pong")

}

fun kick(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    if (event.member?.hasPermission(Permission.KICK_MEMBERS) == true) {
        val mentioned = message.mentionedMembers.first()
        val reason = data.argsContent["reason"]
        val embed = EmbedBuilder().apply {
            setTitle("You have been kicked from ${message.guild.name}")
            addField("Kicked by:", event.author.name, false)
            if (reason != null) addField("Reason", reason, false)
            setColor(Color.RED)
        }.build()

        message.reply(EmbedBuilder().apply {
            setTitle("User ${mentioned.user.name} has been kicked")
            addField("Kicked by:", event.author.name, false)
            if (reason != null) addField("Reason", reason, false)
            setColor(Color.RED)

        }.build())

        mentioned.user.openPrivateChannel().queue {
            it.sendMessage(embed).queue {
                mentioned.kick(reason).queue {
                    runBlocking {
                        Service.insertKickedUser(
                            Kick(
                                user = mentioned.user.idLong,
                                kickedBy = event.author.idLong,
                                reason = reason ?: ""
                            )
                        )
                    }
                }
            }
        }
    }
}

fun ban(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    if (event.member?.hasPermission(Permission.BAN_MEMBERS) == true) {
        val mentioned = message.mentionedMembers.first()
        val reason = data.argsContent["reason"]
        val embed = EmbedBuilder().apply {
            setTitle("You have been banned from ${message.guild.name}")
            addField("Banned by:", event.author.name, false)
            if (reason != null) addField("Reason", reason, false)
            setColor(Color.RED)
        }.build()

        message.reply(EmbedBuilder().apply {
            setTitle("User ${mentioned.user.name} has been banned")
            addField("Banned by:", event.author.name, false)
            if (reason != null) addField("Reason", reason, false)
            setColor(Color.RED)
        }.build())

        mentioned.user.openPrivateChannel().queue {
            it.sendMessage(embed).queue {
                mentioned.ban(0, reason).queue {
                    runBlocking {
                        Service.insertBannedUser(
                            Ban(
                                user = mentioned.user.idLong,
                                bannedBy = event.author.idLong,
                                reason = reason ?: ""
                            )
                        )
                    }
                }
            }
        }
    }
}

fun warn(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    if (event.member?.hasPermission(Permission.MESSAGE_MANAGE) == true) {
        val mentioned = message.mentionedMembers.first()
        val reason = data.argsContent["reason"]
        val embed = EmbedBuilder().apply {
            setTitle("You were warned in ${message.guild.name}")
            addField("Warned by:", event.author.name, false)
            addField("Reason", reason, false)
            setColor(Color.RED)
        }.build()

        message.reply(EmbedBuilder().apply {
            setTitle("User ${mentioned.user.name} was warned")
            setFooter("Warned by: ${event.author.name}")
            addField("Reason", reason, false)
            setColor(Color.RED)
        }.build())

        mentioned.user.openPrivateChannel().queue {
            it.sendMessage(embed).queue {
                runBlocking {
                    Service.warnUser(
                        Warning(
                            user = mentioned.user.idLong,
                            warnedBy = event.author.idLong,
                            reason = reason ?: ""
                        )
                    )
                }
            }
        }
    }
}

fun bans(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    val bannedUsersEmbed = runBlocking {
        val builder = EmbedBuilder().setTitle("Banned users")
        Service.getAllBannedUsers().forEachIndexed { index, it ->
            builder.apply {
                addField(
                    "${index + 1}. User: ${bot.getUserById(it.user)?.name}",
                    "Reason: ${it.reason}\nBanned by: ${bot.getUserById(it.bannedBy)?.name}",
                    false
                )
            }
        }
        builder.build()
    }
    message.reply(bannedUsersEmbed)
}

fun kicks(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    val bannedUsersEmbed = runBlocking {
        val builder = EmbedBuilder().setTitle("Kicked users")
        Service.getAllKickedUsers().forEachIndexed { index, it ->
            builder.apply {
                addField(
                    "${index + 1}. User: ${bot.getUserById(it.user)?.name}",
                    "Reason: ${it.reason}\nKicked by: ${bot.getUserById(it.kickedBy)?.name}",
                    false
                )
            }
        }
        builder.build()
    }
    message.reply(bannedUsersEmbed)
}

fun wantings(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    val bannedUsersEmbed = runBlocking {
        val builder = EmbedBuilder().setTitle("Warnings for user")
        Service.getWarningForUser(message.mentionedMembers.first().idLong)
            .forEachIndexed { index, it ->
                builder.apply {
                    addField(
                        "Warning ${index + 1}:",
                        "Reason: ${it.reason}\nWarned by: ${bot.getUserById(it.warnedBy)?.name}",
                        false
                    )
                }
            }
        builder.build()
    }
    message.reply(bannedUsersEmbed)
}

fun say(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.reply(data.argsContent.getOrElse("message") { "idk" })
}

fun save(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    val msg = data.argsContent["message"] ?: "Something fucked up"
    val author = event.author.idLong
    val toSave = Save(msg, author)
    runBlocking { Service.insert(toSave) }
    message.reply("Saved: $toSave")
}

fun getSaved(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    val author = event.author.idLong
    val list = runBlocking { Service.find(author) }
    val embedBuilder = EmbedBuilder().apply {
        setTitle("Saved shit")
        setColor(Color.WHITE)
        list.forEach { addField(it.data, "By ${it.savedBy}", false) }
    }
    message.reply(embedBuilder.build())
}

fun help(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    val embedBuilder = EmbedBuilder().apply {
        setTitle("title")
        setColor(Color.BLUE)
        setDescription("Desck")
    }
    message.reply(embedBuilder.build())
}
