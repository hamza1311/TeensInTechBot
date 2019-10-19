import discord, logging
from discord.ext import commands
from util.functions import randomDiscordColor # pylint: disable=no-name-in-module
from models import BotConfig
from util.publicCommands import publicCommand # pylint: disable=no-name-in-module

class Miscellaneous(commands.Cog):
    def __init__(self, bot: commands.Bot):
        self.bot = bot

    @publicCommand
    @commands.command()
    async def say(self, ctx: commands.Context, *, content: str):
        """
        Have the bot will respond with whatever argmeument you give it
        """
        await ctx.send(content)
        
    @publicCommand    
    @commands.command()
    async def react(self, ctx: commands.Context, messageId, emoji: str):
        message = await ctx.channel.fetch_message(messageId)
        await message.add_reaction(emoji)

    @commands.Cog.listener()
    async def on_command_error(self, ctx: commands.Context, error: commands.errors.CommandError):
        embed = discord.Embed(
            title=f"A bruh moment happened while processing {ctx.command}",
            color = randomDiscordColor()
        )

        if isinstance(error, commands.errors.CommandNotFound):
            return

        embed.add_field(name = "Error:", value = str(error), inline=False)
            
        await ctx.send(embed=embed)

    @commands.Cog.listener()
    async def on_member_join(self, member: discord.Member):
        config = BotConfig.BotConfig.getForGuild(member.guild.id)
        channel = member.guild.get_channel(config.welcomeChannel)
        await channel.send(f'Welcome to {member.guild.name}, {member.mention}!')
        memberRole = member.guild.get_role(config.memberRoleId)
        member.add_roles(memberRole)

    @commands.Cog.listener()
    async def on_member_remove(self, member):
        config = BotConfig.BotConfig.getForGuild(member.guild.id)
        channel = member.guild.get_channel(config.welcomeChannel)
        await channel.send(f'{member.name} just left :(')
        

def setup(bot: commands.Bot):
    bot.add_cog(Miscellaneous(bot))