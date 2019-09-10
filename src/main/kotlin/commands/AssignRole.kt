package commands

import commands.models.BotCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CommandData

object AssignRole: BotCommand {
    override val help: String
        get() = "yayy"
    override val commandString: String
        get() = "role !member"

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        val message = event.message
        val mentioned = message.mentionedMembers.first()
        event.guild.addRoleToMember(mentioned, event.guild.getRoleById(610885740208849019)!!).queue()
    }
}