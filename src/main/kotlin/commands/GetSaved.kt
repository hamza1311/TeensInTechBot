package commands

import commands.models.BotCommand
import dbshit.Service
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CommandData
import util.reply
import util.startTyping
import java.awt.Color

@Suppress("DuplicatedCode")
object GetSaved : BotCommand {
    override val help: String = "get saved shit from db"
    override val commandString: String = "get"

    override fun command(data: CommandData, event: MessageReceivedEvent) {
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
}