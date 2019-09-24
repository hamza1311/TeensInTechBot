package commands

import commands.models.BotCommand
import commands.models.Category
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CommandData
import util.reply
import util.startTyping

object Ping : BotCommand {
    override val help: String = "Ping Pong"

    override val commandString: String = "ping"
    override val category: Category = Category.PingPong

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        val message = event.message
        message.channel.startTyping()
        Thread.sleep(1000)
        message.reply("Pong")
    }
}