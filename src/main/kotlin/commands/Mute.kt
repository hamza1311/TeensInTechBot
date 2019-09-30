package commands

import commands.models.BotCommand
import commands.models.Category
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CommandData
import util.MUTED_ROLE
import util.reply

object Mute : BotCommand {
    override val help: String = "Mutes the mentioned user"
    override val commandString: String = "mute !user"
    override val category: Category = Category.Moderation

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        if (event.member?.hasPermission(Permission.MANAGE_ROLES) != true) {
            event.message.reply("You don't have the permissions to do that.")
            return
        }
        val mentioned = event.message.mentionedMembers.firstOrNull() ?: error("No user was mentioned")
        event.guild.getRoleById(MUTED_ROLE)?.let { role ->
            event.guild.addRoleToMember(mentioned, role).queue { _ ->
                EmbedBuilder().apply {
                    setTitle("Muted User")
                    addField("User:", mentioned.user.name, false)
                    mentioned.user.avatarUrl?.let { setThumbnail(it) }
                    setFooter("Muted by: ${event.author.name}")
                }.build().also { event.message.reply(it) }
            }
        } ?: error("Muted role with id: $MUTED_ROLE not found")
    }
}

object UnMute : BotCommand {
    override val help: String = "Unmutes the mentioned user"
    override val commandString: String = "unmute !user"
    override val category: Category = Category.Moderation

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        if (event.member?.hasPermission(Permission.MANAGE_ROLES) != true) {
            event.message.reply("You don't have the permissions to do that.")
            return
        }
        val mentioned = event.message.mentionedMembers.firstOrNull() ?: error("No user was mentioned")
        event.guild.getRoleById(MUTED_ROLE)?.let { role ->
            event.guild.removeRoleFromMember(mentioned, role).queue { _ ->
                EmbedBuilder().apply {
                    setTitle("Unmuted user")
                    addField("User:", mentioned.user.name, false)
                    mentioned.user.avatarUrl?.let { setThumbnail(it) }
                    setFooter("Unmuted by: ${event.author.name}")
                }.build().also { event.message.reply(it) }
            }
        } ?: error("Muted role with id: $MUTED_ROLE not found")

    }
}