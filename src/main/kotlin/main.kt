import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.SelfUser

import util.*

private val BOT_TOKEN = System.getenv("BOT_TOKEN")

private lateinit var bot: SelfUser

fun main() {

    commands {

        command("say",  ::say)
        command("ping", ::ping)
        command("help", ::help)

    }

    bot = jda(BOT_TOKEN) {
        eventListeners = arrayOf(CommandProxy.handler)
        activity       = Activity.playing("!ping")
        build().awaitReady().selfUser
    }
}
