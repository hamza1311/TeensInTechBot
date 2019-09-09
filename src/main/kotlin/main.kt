import net.dv8tion.jda.api.JDA
import util.parseCommandTemplate
import net.dv8tion.jda.api.entities.Activity

import util.*

private val BOT_TOKEN = System.getenv("BOT_TOKEN")

lateinit var bot: JDA

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
        command("bans", ::bans)
        command("kicks", ::kicks)
        command("warnings", ::wantings)
        command("help", ::help)
        command("ban !user ?reason", ::ban)
        command("kick !user ?reason", ::kick)
        command("warn !user !reason", ::warn)

    }

    bot = jda(BOT_TOKEN) {
        eventListeners = arrayOf(CommandProxy.handler)
        activity = Activity.playing("!ping")
        build().awaitReady()
    }
}
