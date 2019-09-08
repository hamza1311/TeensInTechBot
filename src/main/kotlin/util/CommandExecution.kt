package util

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Command(val template: CommandTemplate, val block: (CommandData, MessageReceivedEvent) -> Unit)

object CommandProxy {

    val registeredCommands = mutableSetOf<Command>()

    val handler: ListenerAdapter = object : ListenerAdapter() {

        override fun onMessageReceived(ev: MessageReceivedEvent) {
            val cmdString = ev.message.contentRaw

            if (!cmdString.startsWith(BOT_PREFIX)) return

            val call = parseCommandString(cmdString)
            val selectedCommand = registeredCommands.firstOrNull { it.template.name == call.name }

            selectedCommand?.let {
                val data = commandDataFromCall(call, selectedCommand.template)
                selectedCommand.block(data, ev)
            } ?: ev.message.reply("unknown command \"${call.name}\"")
        }

    }

}

fun commands(block: CommandProxy.() -> Unit) = CommandProxy.block()

fun CommandProxy.command(commandString: String, block: (CommandData, MessageReceivedEvent) -> Unit) {
    this.registeredCommands += Command(parseCommandTemplate(commandString), block)
}
