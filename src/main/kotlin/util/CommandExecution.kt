package util

import bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

import java.awt.Color

class Command(val template: CommandTemplate, val block: (CommandData, MessageReceivedEvent) -> Unit)

object CommandProxy {

    val registeredCommands = mutableSetOf<Command>()

    val handler: ListenerAdapter = object : ListenerAdapter() {

        override fun onMessageReceived(ev: MessageReceivedEvent) {
            val cmdString = ev.message.contentRaw

            if (!cmdString.startsWith(BOT_PREFIX) ||
                ev.message.author.idLong == bot.selfUser.idLong
            ) return

            val call = parseCommandString(cmdString)
            val selectedCommand = registeredCommands.firstOrNull { it.template.name == call.name }

            selectedCommand?.let {
                try {
                    val data = commandDataFromCall(call, selectedCommand.template)
                    if (ev.message.contentRaw.endsWith("DEBUG")) ev.message.reply(data.toString())
                    selectedCommand.block(data, ev)
                } catch (e: Exception) {
                    ev.message.reply(EmbedBuilder().run {
                        setTitle("An error occured while parsing/executing command ${selectedCommand.template.name}")
                        addField(e.javaClass.simpleName, e.message, false)
                        addField("Stacktrace", e.stackTrace.joinToString(" ").take(1023), false)
                        setColor(Color.RED)
                        build()
                    })
                }
            } ?: ev.message.reply("unknown command \"${call.name}\"")
        }

    }

}

fun commands(block: CommandProxy.() -> Unit) = CommandProxy.block()

fun CommandProxy.command(commandString: String, block: (CommandData, MessageReceivedEvent) -> Unit) {
    this.registeredCommands += Command(parseCommandTemplate(commandString), block)
}
