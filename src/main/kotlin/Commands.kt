import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.*
import java.awt.Color

fun ping(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    Thread.sleep(1000)
    message.reply("Pong")

}

fun say(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.reply(data.argsContent.getOrElse("message") { "idk" })
}

fun help(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    val embedBuilder = EmbedBuilder().apply {
        setTitle("title")
        setColor(Color.BLUE)
        setDescription("Desck")
    }
    message.reply(embedBuilder.build())
}
