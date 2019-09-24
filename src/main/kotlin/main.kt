import commands.*
import dbshit.Service
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import util.parseCommandTemplate
import net.dv8tion.jda.api.entities.Activity

import util.*
import java.io.File

lateinit var bot: JDA

fun main() {
    commands {
        commands(Ban, Bans, Kick, Kicks, Warn, Warnings, UnWarn)
        commands(Purge, Roles, AssignRole, RemoveRole, AddSelfAssignRole, Mute, UnMute)
        command(Ping)
        command(Save)
        command(GetSaved)
        command(Help)
    }

    bot = jda(File("/run/secrets/token").readText()) {
        eventListeners = arrayOf(CommandProxy.handler)
        activity = Activity.playing("!ping")
        build().awaitReady()
    }
}
