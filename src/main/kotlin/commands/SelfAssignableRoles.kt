package commands

import commands.models.BotCommand
import dbshit.Role
import dbshit.Service
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import util.CommandData
import util.reply

object Roles : BotCommand {
    override val help: String = "Get all the self-assignable roles"
    override val commandString: String = "roles"

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        val roles = runBlocking { Service.getAllSelfAssignRoles() }
        val embed = EmbedBuilder().apply {
            setTitle("Available self-assignable roles")
            setFooter("Requested by: ${event.author.name}")
        }
        roles.forEachIndexed { index, role ->
            embed.addField(
                "${index + 1}. ${role.name}",
                "Currently assigned to: ${role.assignedTo.map { it.username }}",
                false
            )
        }
        event.message.reply(embed.build())
    }
}

object AssignRole : BotCommand {
    override val help: String = "Give a self-assignable role"
    override val commandString: String = "assignrole !role"

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        val role = runBlocking {
            data.argsContent["role"]?.let { Service.getSelfAssignRoleByName(it) }
                ?: error("role param is null")
        }
        println(role)
        role.assignedTo.add(Role.User(event.author.name, event.author.idLong))
        println(role)
        event.message.reply(role.toString())
        runBlocking { Service.updateAssignedRole(role) }
        val guildRole = event.guild.getRoleById(role.id)
        val member = event.member
        if (member != null && guildRole != null) {
            event.guild.addRoleToMember(member, guildRole).queue()
        } else {
            error("Either member or guildRole is null. epic bruh moment")
        }
    }
}

object RemoveRole : BotCommand {
    override val help: String = "Remove a self-assignable role"
    override val commandString: String = "removerole !role"

    override fun command(data: CommandData, event: MessageReceivedEvent) {

    }
}

object AddSelfAssignRole : BotCommand {
    override val help: String = "Adds a new self-assignable role"
    override val commandString: String = "addrole !role"

    override fun command(data: CommandData, event: MessageReceivedEvent) {
        val mentionedRole = event.message.mentionedRoles.firstOrNull()
            ?: error("More than one role mentioned role found")
        val role = Role(mentionedRole.name, mentionedRole.idLong, mutableSetOf())
        runBlocking { Service.addRole(role) }
        event.message.reply(EmbedBuilder().apply {
            setTitle("Successfully added self-assignable role")
            addField("Role name: ", role.name, false)
            addField("Role id: ", "${role.id}", false)
            setFooter("Added by: ${event.author.name}")
        }.build())
    }
}