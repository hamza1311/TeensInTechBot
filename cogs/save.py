# pylint: disable=no-member
import discord
from discord.ext import commands
from models.Save import Save
from mongoengine import DoesNotExist
from util.functions import randomDiscordColor # pylint: disable=no-name-in-module

class SaveStuff(commands.Cog):
    def __init__(self, bot: commands.Bot):
        self.bot = bot

    @commands.command()
    async def save(self, ctx: commands.Context, content: str):
        """
        Save stuff to DB
        """
        save = Save(savedBy = ctx.author.id, data = content)
        save.save()
        embed = discord.Embed(title='Saved', color = randomDiscordColor())
        embed.add_field(name = 'data', value=save.data)
        embed.add_field(name = 'saved by', value=ctx.author.mention)
        await ctx.send(embed=embed)


    @commands.command()
    async def get(self, ctx: commands.Context):
        """
        Get saved stuff for the author from DB
        """
        out = ''
        for i in Save.objects(savedBy = ctx.author.id):
            out += i.data + '\n'

        embed = discord.Embed(color = randomDiscordColor())
        embed.add_field(name = 'Stuff saved', value=out, inline=False)
        await ctx.send(embed = embed)
    
    @commands.command()
    async def delete(self, ctx: commands.Context, content: str):
        """
        Delete something that was saved previously
        """
        try:
            Save.objects(savedBy = ctx.author.id, data=content).get().delete()
            await ctx.send(f'Deleted doc')
        except DoesNotExist as identifier:
            await ctx.send(str(identifier))

    

def setup(bot: commands.Bot):
    bot.add_cog(SaveStuff(bot))