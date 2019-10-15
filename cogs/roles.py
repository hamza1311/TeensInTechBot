import discord
from discord.ext import commands
from models import Role, BotConfig

class Roles(commands.Cog):
    def __init__(self, bot: commands.Bot):
        self.bot = bot
        roles = Role.Role.objects # pylint: disable=no-member
        self.reactRoles = []
        for role in roles:
            self.reactRoles.append(role)

    @commands.Cog.listener()
    async def on_raw_reaction_add(self, payload: discord.RawReactionActionEvent):
        messageId = payload.message_id
        user = payload.user_id
        guild = payload.guild_id
        emoji = str(payload.emoji)
        channel = payload.channel_id

        if channel == BotConfig.BotConfig.getForGuild(guildId=guild).rolesChannelId:
            guild = self.bot.get_guild(guild)
            for role in self.reactRoles:
                if role.messageId == messageId:
                    member = guild.get_member(user)
                    channel = guild.get_channel(channel)
                    message = await channel.fetch_message(messageId)
                    if role.roleEmoji == emoji:
                        await member.add_roles(guild.get_role(role.roleId))
                        role.update(push__assignedTo=user)
                        return
                    else:
                        await message.remove_reaction(emoji, member)
    
    
    @commands.Cog.listener()
    async def on_raw_reaction_remove(self, payload):
        messageId = payload.message_id
        userId = payload.user_id
        guildId = payload.guild_id
        emoji = str(payload.emoji)
        channel = payload.channel_id
        
        if channel == BotConfig.BotConfig.getForGuild(guildId=guildId).rolesChannelId:
            guild = self.bot.get_guild(guildId)
            for role in self.reactRoles:
                if role.messageId == messageId:
                    member = guild.get_member(userId)
                    if role.roleEmoji == emoji:
                        await member.remove_roles(guild.get_role(role.roleId))
                        role.update(pull__assignedTo=userId)
                        return
                

    @commands.command()
    @commands.is_owner()
    async def addRole(self, ctx: commands.Context, role: discord.Role, emoji: str, messageId: int):
        print(emoji)
        reactRole = Role.Role(
            roleId = role.id,
            roleEmoji = emoji,
            assignedTo = [],
            messageId = messageId
        )

        reactRole.save()
        await ctx.send('Saved')


def setup(bot: commands.Bot):
    bot.add_cog(Roles(bot))