import discord, asyncio, re
from discord.ext import commands
from util.functions import randomDiscordColor

class Moderation(commands.Cog):
    def __init__(self, bot: commands.Bot):
        self.bot = bot

    @commands.command()
    async def ping(self, ctx: commands.Context):
        await ctx.send('pong')

    @commands.command()
    @commands.has_permissions(kick_members=True)
    async def kick(self, ctx: commands.Context, victim: discord.Member, *, reason:str = None):
        """
        Yeet a user
        """

        msg = f"You have been kicked from {ctx.guild.name}"
        if reason:
            msg += f" for `{reason}`"

        try:
            await victim.send(msg)
        except discord.Forbidden:
            await ctx.send("❗ I can't dm that user")

        await ctx.send(
            f"**User {victim.mention} has been kicked by {ctx.author.mention}**")
        await victim.kick(reason=reason)

        # save kicked user to db

    @commands.command()
    @commands.has_permissions(ban_members=True)
    async def ban(self, ctx: commands.Context, victim: discord.Member, *, reason: str = None):
        """
        Ban a user
        """

        if victim.id == ctx.author.id:
            await ctx.send("You can't ban yourself")
            return
        
        victim.ban(reason=reason)
        
        await ctx.send(f"**User {victim.mention} has been banned from the server by {ctx.author.mention}**")

        try:
            msg = f"You have been banned from {ctx.guild.name}"
            if reason:
                msg += f" for `{reason}`"
            await victim.send(msg)
        except discord.Forbidden:
            await ctx.send("I can't DM that user. Banned without notice")
        
        # save to db


    @commands.command()
    async def mute(self, ctx: commands.Context, victim: discord.Member, *, reasonAndDuration: str = None):
        """
        Mute a user
        Duration must be given in seconds. Use a calculator, not me.
        Mute will become permanent if bot script is restarted
        """

        duration = re.search(f'([0-9]+)? ?', reasonAndDuration).group(0).strip()
        reason = reasonAndDuration[len(duration):].strip()

        if victim.id == ctx.author.id:
            await ctx.send("Why do want to mute yourself?\nI'm not gonna let you do it")
            return
        
        muted = ctx.guild.get_role(597012103492272128)
        
        out = f"**User {victim.mention} has been muted by {ctx.author.mention}**"
        
        await victim.add_roles(muted)
        for channel in ctx.guild.channels:
                await channel.set_permissions(victim, send_messages = False, add_reactions = False)

        await ctx.send(out)

        try:
            msg = f"You have been muted in {ctx.guild.name}"
            if reason:
                msg += f" for `{reason}`"

            # await victim.send(msg)
        except discord.Forbidden:
            await ctx.send("I can't DM that user. Muted without notice")
        
        # save to db

        if duration:
            await asyncio.sleep(int(duration))
            await victim.remove_roles(muted)
            for channel in ctx.guild.channels:
                await channel.set_permissions(victim, overwrite=None)
            
    
    @commands.command()
    async def unmute(self, ctx: commands.Context, victim: discord.Member):
        """
        Unmute a user
        """
        muted = ctx.guild.get_role(597012103492272128)

        await victim.remove_roles(muted)
        for channel in ctx.guild.channels:
            await channel.set_permissions(victim, overwrite=None)

        out = f"**User {victim.mention} has been unmuted by {ctx.author.mention}**"

        await ctx.send(out)

    @commands.command()
    async def purge(self, ctx: commands.Context, amount: int):
        if ctx.channel.permissions_for(ctx.author).manage_messages:
            await ctx.channel.purge(limit=amount + 1)

            desc = f"**{amount + 1} messages were deleted in {ctx.channel.mention} by {ctx.author.mention}**"
            embed = discord.Embed(color = randomDiscordColor(), description=desc)
            await ctx.send(embed=embed)

def setup(bot: commands.Bot):
    bot.add_cog(Moderation(bot))