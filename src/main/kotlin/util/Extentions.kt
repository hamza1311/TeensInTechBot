package util

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.*
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

fun jda(token: String, block: JDABuilder.() -> SelfUser): SelfUser {
    return JDABuilder(token).block()
}

var JDABuilder.eventListeners
    get() = arrayOf<Any>()
    set(value) = this.addEventListeners(*value).run { Unit }

var JDABuilder.activity
    get() = Activity.listening("none")
    set(value) = this.setActivity(value).run { Unit }
