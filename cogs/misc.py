import discord
from discord.ext import commands
from util.functions import randomDiscordColor # pylint: disable=no-name-in-module

class Miscellaneous(commands.Cog):
    def __init__(self, bot: commands.Bot):
        self.bot = bot

    @commands.command()
    async def say(self, ctx: commands.Context, content: str):
        await ctx.send(content)

    @commands.Cog.listener()
    async def on_command_error(self, ctx: commands.Context, error: commands.errors.CommandError):
        embed = discord.Embed(
            title=f"A bruh moment happened while processing {ctx.command}",
            color = randomDiscordColor()
        )

        try:
            raise error
        except Exception as e:
            if isinstance(error, commands.errors.CommandNotFound):
                embed.title = f"A bruh moment happened: You probably made a typo"

            embed.add_field(name = "Error:", value = str(e), inline=False)
        
        await ctx.send(embed=embed)

    @commands.Cog.listener()
    async def on_member_join(self, member):
        channel = member.guild.get_channel(0)
        await channel.send(f'Welcome to {member.guild.name}, {member.mention}!')

    @commands.Cog.listener()
    async def on_member_remove(self, member):
        channel = member.guild.get_channel(0)
        await channel.send(f'{member.name} just left :(')
        

def setup(bot: commands.Bot):
    bot.add_cog(Miscellaneous(bot))