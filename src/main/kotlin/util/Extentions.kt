package util

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent


fun MessageReceivedEvent.addCommand(command: String, callback: (MessageReceivedEvent) -> Unit): Boolean {
    if (message.contentRaw.startsWith("$BOT_PREFIX$command")) {
        callback(this)
        return true
    } else {
        return false
    }
}

fun Message.reply(text: String) = channel.sendMessage(text).queue()
fun Message.reply(embed: MessageEmbed) = channel.sendMessage(embed).queue()

fun MessageChannel.startTyping() = sendTyping().queue()

val Message.content
    get() = this.contentRaw.split(" ")[1]