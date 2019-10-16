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
        """
        Configure the bot.
        Asks the required IDs for channels and stores them in database
        """

        def pred(m):
            return m.author == ctx.author and m.channel == ctx.channel

        await ctx.send('What channel do you want to use for self assignable roles? Please enter the channel ID')
        rolesCnlId = await self.bot.wait_for('message', check=pred)

        await ctx.send('What are the role IDs for mod roles? Please the role IDs with commas')
        modIdsMsg = await self.bot.wait_for('message', check=pred)
        modIds = [int(str(id).strip()) for id in str(modIdsMsg.content).split(',')]

        await ctx.send('What channel do you want to use for welcome message? Please enter the channel ID')
        welcomeCnlId = await self.bot.wait_for('message', check=pred)

        await ctx.send('What is the role ID for muted role')
        mutedRoleId = await self.bot.wait_for('message', check=pred)

        await ctx.send('What is the role ID for member role?')
        memberRoleId = await self.bot.wait_for('message', check=pred)

        botConfig = models.BotConfig.BotConfig(
            serverId = ctx.guild.id,
            rolesChannelId = rolesCnlId.content,
            modIds = modIds,
            welcomeChannel = welcomeCnlId.content,
            memberRoleId = memberRoleId.content,
            mutedRoleId = mutedRoleId.content,
        )

        botConfig.save()

        await ctx.send(f'Great! The channel for self assignable roles is {botConfig.rolesChannelId}\nMod IDs are {botConfig.modIds}\nWelcome channel is {botConfig.welcomeChannel}\nMuted Role Id is {botConfig.mutedRoleId}\nMember Role ID is {botConfig.memberRoleId}')


def setup(bot: commands.Bot):
    bot.add_cog(BotConfig(bot))