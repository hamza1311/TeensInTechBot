package commands

import commands.models.BotCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CommandData
import java.util.stream.Collectors

object Purge : BotCommand {
    override val help: String = "Delete certain amount of messages"

    override val commandString: String = "purge !amount"

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        val amount = data.argsContent["amount"]?.toLong() ?: 0L
        val messages = event.channel.iterableHistory.stream().limit(amount).collect(Collectors.toList())
        messages.forEach {
            it.delete().queue()
        }
    }
}