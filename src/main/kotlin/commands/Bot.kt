package commands

import bot
import commands.models.BotCommand
import commands.models.Category
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CAN_MODIFY_BOT
import util.CommandData
import util.reply

object Nickname : BotCommand {
    override val help: String = "Changes bot nickname"
    override val commandString: String = "nick !name"
    override val category: Category = Category.Bot

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        if (!CAN_MODIFY_BOT.contains(event.author.idLong)) {
            event.message.reply("You're not allowed to do that")
        }
        event.guild.modifyNickname(
            event.guild.getMember(bot.selfUser) ?: error("This must not happen"),
            data.argsContent["name"]
        ).queue {
            event.message.reply("${event.author.asMention} changed my nickname to ${data.argsContent["name"]}")
        }
    }
}

object ActivityStatus : BotCommand {
    override val help: String = """Changes bot activity status 
        Valid activity status categories are:
        1. watching
        2. playing
        3. listening
    """.trimMargin()
    override val commandString: String = "activity !category !status"
    override val category: Category = Category.Bot

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        if (!CAN_MODIFY_BOT.contains(event.author.idLong)) {
            event.message.reply("You're not allowed to do that")
            return
        }
        val status = data.argsContent["status"] ?: error("Status is null")
        bot.presence.activity = when (data.argsContent["category"]) {
            "watching" -> Activity.watching(status)
            "playing" -> Activity.playing(status)
            "listening" -> Activity.listening(status)
            else -> error("incorrect/misspelled activity provided by ${event.author.asMention}")
        }.also {
            event.message.reply("Changed bot activity status to\n${data.argsContent["category"]}: $status")
        }
    }
}