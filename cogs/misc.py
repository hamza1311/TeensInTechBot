import discord
from discord.ext import commands

class Miscellaneous(commands.Cog):
    def __init__(self, bot: commands.Bot):
        self.bot = bot

    @commands.command()
    async def say(self, ctx: commands.Context, content: str):
        await ctx.send(content)

def setup(bot: commands.Bot):
    bot.add_cog(Miscellaneous(bot))