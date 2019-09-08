import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import services.Save
import services.Service
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

fun save(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    val msg = data.argsContent["message"] ?: "Something fucked up"
    val author = event.author.idLong
    val toSave = Save(msg, author)
    runBlocking { Service.insert(toSave) }
    message.reply("Saved: $toSave")
}

fun getSaved(data: CommandData, event: MessageReceivedEvent) {
    val message = event.message
    message.channel.startTyping()
    val author = event.author.idLong
    val list = runBlocking { Service.find(author) }
    val embedBuilder = EmbedBuilder().apply {
        setTitle("Saved shit")
        setColor(Color.WHITE)
        list.forEach { addField(it.data, "By ${it.savedBy}", false) }
    }
    message.reply(embedBuilder.build())
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
