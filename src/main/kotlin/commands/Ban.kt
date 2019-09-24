package commands

import bot
import commands.models.BotCommand
import commands.models.Category
import dbshit.Ban
import dbshit.Service
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CommandData
import util.reply
import util.startTyping
import java.awt.Color
import java.util.*
import kotlin.concurrent.schedule

object Ban : BotCommand {
    override val help: String = "Bans the mentioned user"
    override val commandString: String = "ban !user ?reason ?delayinmills"
    override val category: Category = Category.Moderation

    override fun command(data: CommandData, event: MessageReceivedEvent) {
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

                    mentioned.ban(0, reason).queue { _ ->
                        runBlocking {
                            Service.insertBannedUser(
                                Ban(
                                    user = mentioned.user.idLong,
                                    bannedBy = event.author.idLong,
                                    reason = reason ?: ""
                                )
                            )
                        }
                        data.argsContent["delayinmills"]?.let { delay ->
                            Timer().schedule(delay.toLong()) {
                                event.guild.unban(mentioned.id).queue()
                            }
                        }
                    }
                }
            }
        }
    }
}

object Bans : BotCommand {
    override val help: String = "get all bans by bot"
    override val commandString: String = "bans"
    override val category: Category = Category.Moderation

    override fun command(data: CommandData, event: MessageReceivedEvent) {
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
}