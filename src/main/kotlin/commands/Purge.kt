package commands

import commands.models.BotCommand
import commands.models.Category
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CommandData
import util.reply
import java.util.stream.Collectors

object Purge : BotCommand {
    override val help: String = "Delete the given amount of messages"
    override val commandString: String = "purge !amount"
    override val category: Category = Category.Moderation

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        if (event.member?.hasPermission(Permission.MESSAGE_MANAGE) == true) {
            val amount = data.argsContent["amount"]?.toLong() ?: 0L
            val messages = event.channel.iterableHistory.stream().limit(amount).collect(Collectors.toList())
            messages.forEach {
                it.delete().queue {
                    event.message.reply("User ${event.author.asMention} deleted ${messages.size} messages")
                }
            }
        } else {
            event.message.reply("Bruh you ain't got the permission for it")
        }
    }
}