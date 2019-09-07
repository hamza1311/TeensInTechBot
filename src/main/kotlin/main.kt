import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.SelfUser
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import util.addCommand

private val BOT_TOKEN = System.getenv("BOT_TOKEN")
private lateinit var bot: SelfUser

fun main() {
    bot = JDABuilder(BOT_TOKEN)
        .addEventListeners(Bot())
        .setActivity(Activity.playing("!ping"))
        .build().awaitReady().selfUser
}

class Bot : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.member?.idLong == bot.idLong) return

        event.addCommand("say", ::say).also { if (it) return }
        event.addCommand("ping", ::ping).also { if (it) return }
        event.addCommand("help", ::help).also { if (it) return }
    }
}
