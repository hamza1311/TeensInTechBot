package commands

import commands.models.BotCommand
import commands.models.Category
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CommandData
import util.CommandProxy
import util.reply
import java.awt.Color

object Help : BotCommand {
    override val help: String = "Shows this message"
    override val commandString: String = "help"
    override val category: Category = Category.Help

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        val registeredCommands = CommandProxy.registeredCommands
        val color = Color.PINK

        val commands = registeredCommands.groupBy { it.category }
        val embeds = mutableListOf<MessageEmbed>()

        commands.forEach {
            val embedBuilder = EmbedBuilder()
                .setTitle(it.key.name)
                .setColor(color)

            it.value.forEach { command ->
                embedBuilder.addField(command.template.name, "${command.helpMessage} ", false)
            }

            embeds.add(embedBuilder.build())
        }
        event.message.reply("You wanted help? Help is provided to you")
        embeds.forEach { event.message.reply(it) }
    }
}