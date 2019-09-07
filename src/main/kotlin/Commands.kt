import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.*
import java.awt.Color

fun ping(event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    Thread.sleep(1000)
    message.reply("Pong")

}

fun say(event: MessageReceivedEvent) {
    val message = event.message
    message.reply(message.content)
}

fun help(event: MessageReceivedEvent) {
    val message = event.message
    val embedBuilder = EmbedBuilder().apply {
        setTitle("title")
        setColor(Color.BLUE)
        setDescription("Desck")
    }
    message.reply(embedBuilder.build())
}
