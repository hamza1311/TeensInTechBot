package commands

import bot
import commands.models.BotCommand
import commands.models.Category
import dbshit.Kick
import dbshit.Service
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CommandData
import util.reply
import util.startTyping
import java.awt.Color

object Kick: BotCommand {
    override val help: String = "Kick the mentioned user"
    override val commandString: String = "kick !user ?reason"
    override val category: Category = Category.Moderation

    override fun command(data: CommandData, event: MessageReceivedEvent) {
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
}

object Kicks: BotCommand {
    override val help: String = "Get all the kicks performed by me"
    override val commandString: String = "kicks"
    override val category: Category = Category.Moderation

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        val message = event.message
        message.channel.startTyping()
        val kickedUsersEmbed = runBlocking {
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
        message.reply(kickedUsersEmbed)
    }
}