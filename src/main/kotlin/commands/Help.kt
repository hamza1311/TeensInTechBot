package commands

import commands.models.BotCommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CommandData
import util.CommandProxy
import util.reply

object Help : BotCommand {
    override val help: String = "Shows this message"
    override val commandString: String = "help"

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        val commands = CommandProxy.registeredCommands
        val embedBuilder = EmbedBuilder().setTitle("You wanted help? Help is provided to you")
        commands.forEach { command ->
            embedBuilder.addField(command.template.name, "${command.helpMessage} ", false)
        }
        event.message.reply(embedBuilder.build())
    }
}