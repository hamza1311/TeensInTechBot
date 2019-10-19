import discord, random
from mongoengine import connect
from discord.ext import commands
# from keys import bot as BOT_TOKEN
from util.functions import randomDiscordColor, isMod # pylint: disable=no-name-in-module

bot = commands.Bot(command_prefix="!", case_insensitive=True,
                   owner_ids=[529535587728752644])

cogs = ["moderation", "misc", "save", "roles"]


@bot.event
async def on_ready():
    print(f"{bot.user.name} is running")
    print("-"*len(bot.user.name + " is running"))
    await bot.change_presence(
        status=discord.Status(discord.Status.online.__str__()),
        activity=discord.Game(f"use {bot.command_prefix}help")
    )
    bot.remove_command('help')

    for i in cogs:
        bot.load_extension(f"cogs.{i}")
    
    bot.load_extension(f"bot") # Load cog for bot. This is at root of the project, not in /cogs because otherwise reload command doesn't work

client = connect('test', host = 'db', port = 27017)

with open('/run/secrets/token') as file:
    BOT_TOKEN = file.read()
bot.run(BOT_TOKEN)
