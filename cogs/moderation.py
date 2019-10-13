import discord, asyncio, re, time
from discord.ext import commands
from datetime import datetime
from util.functions import randomDiscordColor # pylint: disable=no-name-in-module
from models.Ban import Ban

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
    async def ban(self, ctx: commands.Context, victim: discord.Member, *, reasonAndDuration: str = ""):
        """
        Ban a user
        """

        if victim.id == ctx.author.id:
            await ctx.send("Why do want to ban yourself?\nI'm not gonna let you do it")
            return
        
        duration = re.search(f'([0-9]+)? ?', reasonAndDuration).group(0).strip()
        reason = reasonAndDuration[len(duration):].strip()
        print(duration)

        # await victim.ban(reason=reason)

        ban = Ban(
                reason = reason if reason else None,
                bannedAt = int(time.time()),
                bannedById = ctx.author.id,
                bannedByUsername = ctx.author.name,
                bannedUserId = victim.id,
                bannedUserUsername = victim.name,
                unbanTime = (int(time.time()) + int(duration)),
            )
        
        ban.save()

        embedColor = randomDiscordColor()

        embed = discord.Embed(title=f"User was banned from {ctx.guild.name}", color = embedColor)
        embed.add_field(name = 'Banned By', value = ctx.author.mention, inline = True)
        embed.add_field(name = 'Banned user', value = victim.mention, inline = True)
        if reason: embed.add_field(name = 'Reason', value = reason, inline = False)
        if (ban.unbanTime != 0): embed.add_field(name = 'Banned till', value = datetime.fromtimestamp(ban.unbanTime).strftime("%d-%m-%Y, %H:%M:%S"), inline = False)
        embed.set_footer(text = f'Banned at {datetime.fromtimestamp(ban.bannedAt).strftime("%d-%m-%Y, %H:%M:%S")}')
        embed.set_thumbnail(url = victim.avatar_url)

        await ctx.send(embed = embed)

        try:
            embed = discord.Embed(title = f"You have been banned from {ctx.guild.name}", color = embedColor)
            if reason: embed.add_field(name = 'Reason', value = reason, inline = False)
            embed.set_footer(text = f'Banned at {datetime.fromtimestamp(ban.bannedAt).strftime("%d-%m-%Y, %H:%M:%S")}')
            if (ban.unbanTime != 0): embed.add_field(name = 'Banned till', value = datetime.fromtimestamp(ban.unbanTime).strftime("%d-%m-%Y, %H:%M:%S"), inline = False)
            embed.add_field(name = 'Banned By', value = ctx.author.mention, inline = False)

            await ctx.send(embed = embed)

        except discord.Forbidden:
            await ctx.send("I can't DM that user. Banned without notice")

        if duration:
            await asyncio.sleep(int(duration))
            await victim.unban()


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