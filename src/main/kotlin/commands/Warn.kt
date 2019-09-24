package commands

import bot
import commands.models.BotCommand
import dbshit.Service
import dbshit.Warning
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CommandData
import util.reply
import util.startTyping
import java.awt.Color

object Warn : BotCommand {
    override val help: String = "warn user"
    override val commandString: String = "warn !user !reason"

    override fun command(data: CommandData, event: MessageReceivedEvent) {
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
}

object Warnings : BotCommand {
    override val help: String = "get warnings of a user"
    override val commandString: String = "warnings !user"

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        val message = event.message
        message.channel.startTyping()
        val bannedUsersEmbed = runBlocking {
            val builder = EmbedBuilder().setTitle("Warnings for user")
            Service.getWarningForUser(message.mentionedMembers.first().idLong)
                .forEachIndexed { index, it ->
                    builder.apply {
                        addField(
                            "Warning ${index + 1}:",
                            "Reason: ${it.reason}\nWarned by: ${bot.getUserById(it.warnedBy)?.name}\nWarningId: ${it.id}",
                            false
                        )
                    }
                }
            builder.build()
        }
        message.reply(bannedUsersEmbed)
    }
}

object UnWarn : BotCommand {
    override val help: String = "Delete a warning"
    override val commandString: String = "unwarn !warningid"

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        event.message.channel.startTyping()
        val warningId = data.argsContent["warningid"] ?: error("Warning ID is null")
        runBlocking {
            val warningToBeRemoved = Service.getWarningById(warningId)
            val embedBuilder = EmbedBuilder().apply {
                setTitle("Removed Warning")
                addField("Warning reason", warningToBeRemoved?.reason, false)
                addField("Warned user", "${bot.getUserById(warningToBeRemoved!!.user)?.name}", false)
                setFooter("Successfully removed by: ${event.author.name}")
            }
            Service.removeWarning(warningId).also {
                event.message.reply(embedBuilder.build())
            }
        }
    }
}