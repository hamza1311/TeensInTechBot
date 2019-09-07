package util

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Command(val cmdString: String, val block: (MessageReceivedEvent) -> Unit)

object CommandProxy {

    val registeredCommands = mutableSetOf<Command>()

    val handler: ListenerAdapter = object : ListenerAdapter() {

        override fun onMessageReceived(ev: MessageReceivedEvent) {
            registeredCommands
                .first { cmd -> ev.message.contentRaw == BOT_PREFIX + cmd.cmdString }
                .let   { it.block(ev) }
        }

    }

}

fun commands(block: CommandProxy.() -> Unit) = CommandProxy.block()

fun CommandProxy.command(commandString: String, block: (MessageReceivedEvent) -> Unit) {
    this.registeredCommands += Command(commandString, block)
}
