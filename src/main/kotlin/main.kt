import util.parseCommandTemplate
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.SelfUser

import util.*

private val BOT_TOKEN = System.getenv("BOT_TOKEN")

lateinit var bot: SelfUser

fun main() {
    val template = parseCommandTemplate("send ?in")
    println("template: $template")

    val input = "send lucy hello"
    println("input: $input")

    val call = parseCommandString(input)
    println("call: $call")

    val data = commandDataFromCall(call, template)
    println("data: $data")

    commands {

        command("say !message", ::say)
        command("save !message", ::save)
        command("get", ::getSaved)
        command("ping", ::ping)
        command("help", ::help)

    }

    bot = jda(BOT_TOKEN) {
        eventListeners = arrayOf(CommandProxy.handler)
        activity = Activity.playing("!ping")
        build().awaitReady().selfUser
    }
}
