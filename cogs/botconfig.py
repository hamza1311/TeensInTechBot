import discord
from discord.ext import commands
from util.functions import randomDiscordColor # pylint: disable=no-name-in-module
import models

class BotConfig(commands.Cog):
    def __init__(self, bot: commands.Bot):
        self.bot = bot

    @commands.command()
    @commands.is_owner()
    async def config(self, ctx: commands.Context):
        await ctx.send('What channel do you want to use for self assignable roles? Please enter the channel ID')

        def pred(m):
            return m.author == ctx.author and m.channel == ctx.channel

        rolesCnlId = await self.bot.wait_for('message', check=pred)
        await ctx.send('What are the role IDs for mod roles? Please the role IDs with commas')
        modIdsMsg = await self.bot.wait_for('message', check=pred)
        modIds = [int(str(id).strip()) for id in str(modIdsMsg.content).split(',')]

        await ctx.send('What channel do you want to use for welcome message? Please enter the channel ID')
        welcomeCnlId = await self.bot.wait_for('message', check=pred)
        botConfig = models.BotConfig.BotConfig(
            serverId = ctx.guild.id,
            rolesChannelId = rolesCnlId.content,
            modIds = modIds,
            welcomeChannel = welcomeCnlId.content,
        )

        botConfig.save()

        await ctx.send(f'Great! The channel for self assignable roles is {botConfig.rolesChannelId}\nMod IDs are {botConfig.modIds}\nWelcome channel is {botConfig.welcomeChannel}')

    @commands.command()
    @commands.is_owner()
    async def upateRolesConfig(self, ctx: commands.Context, newRolesChannelId):
        models.BotConfig.BotConfig.getForGuild(ctx.guildId).update(set__rolesChannelId=newRolesChannelId)
        await ctx.send(f'Updated the rolesChannelId to {newRolesChannelId}')


def setup(bot: commands.Bot):
    bot.add_cog(BotConfig(bot))