import commands.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity

import util.*
import java.io.File

lateinit var bot: JDA

fun main() {
    commands {
        commands(Ban, Bans, Kick, Kicks, Warn, Warnings, UnWarn)
        commands(Purge, Roles, AssignRole, RemoveRole, AddSelfAssignRole, Mute, UnMute)
        commands(Nickname, ActivityStatus)
        commands(Save, GetSaved)
        commands(Ping)
        command(Help)
    }

    bot = jda(File("/run/secrets/token").readText().trim()) {
        eventListeners = arrayOf(CommandProxy.handler)
        activity = Activity.watching("over tit")
        build().awaitReady()
    }
}
