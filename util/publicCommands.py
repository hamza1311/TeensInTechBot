from discord.ext.commands import Command

publicCommands = set()

def publicCommand(command: Command):
    publicCommands.add(command.name)
    return command