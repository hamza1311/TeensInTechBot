package util

import bot
import commands.models.BotCommand
import commands.models.Category
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

import java.awt.Color

class Command(
    val template: CommandTemplate,
    val block: (CommandData, MessageReceivedEvent) -> Unit,
    val helpMessage: String,
    val category: Category
)

object CommandProxy {

    val registeredCommands = mutableSetOf<Command>()

    val handler: ListenerAdapter = object : ListenerAdapter() {

        override fun onMessageReceived(ev: MessageReceivedEvent) {
            val cmdString = ev.message.contentRaw

            if (!cmdString.startsWith(BOT_PREFIX) ||
                ev.message.author.idLong == bot.selfUser.idLong
            ) return

            val call = parseCommandString(cmdString)
            val selectedCommand = registeredCommands.firstOrNull { it.template.name == call.name }

            selectedCommand?.let {
                try {
                    val data = commandDataFromCall(call, selectedCommand.template)
                    if (ev.message.contentRaw.endsWith("DEBUG")) ev.message.reply(data.toString())
                    selectedCommand.block(data, ev)
                } catch (e: Exception) {
                    ev.message.reply(EmbedBuilder().run {
                        setTitle("A bruh moment while parsing/executing command ${selectedCommand.template.name}")
                        addField(e.javaClass.simpleName, e.message, false)
                        addField("Stacktrace", e.stackTrace.joinToString(" ").take(1023), false)
                        setColor(Color.RED)
                        build()
                    })
                }
            } ?: ev.message.reply("unknown command \"${call.name}\"")
        }

        override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
            event.guild.getTextChannelById(WELCOME_CHANNEL)?.sendMessage("Welcome to ${event.guild.name}!!!!!!")
                ?.queue()
            event.guild.getRoleById(MEMBER_ROLE)?.let { event.guild.addRoleToMember(event.member, it).queue() }
        }

    }

}

fun commands(block: CommandProxy.() -> Unit) = CommandProxy.block()

fun CommandProxy.command(botCommand: BotCommand) {
    this.registeredCommands += Command(
        parseCommandTemplate(botCommand.commandString),
        botCommand::command,
        botCommand.help,
        botCommand.category
    )
}

fun CommandProxy.commands(vararg botCommands: BotCommand) {
    botCommands.forEach {
        this.registeredCommands += Command(
            parseCommandTemplate(it.commandString),
            it::command,
            it.help,
            it.category
        )
    }
}
