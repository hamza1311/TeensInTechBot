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

        if user == self.bot.user.id:
            return

        if channel == BotConfig.BotConfig.getForGuild(guildId=guild).rolesChannelId:
            guild = self.bot.get_guild(guild)
            for role in self.reactRoles:
                if role.messageId == messageId:
                    member = guild.get_member(user)
                    channel = guild.get_channel(channel)
                    if role.roleEmoji == emoji:
                        await member.add_roles(guild.get_role(role.roleId))
                        role.update(push__assignedTo=user)
                        return
    
    
    @commands.Cog.listener()
    async def on_raw_reaction_remove(self, payload):
        messageId = payload.message_id
        userId = payload.user_id
        guildId = payload.guild_id
        emoji = str(payload.emoji)
        channel = payload.channel_id
        
        if userId == self.bot.user.id:
            return

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
        """
        Add a new role for self assignable roles
        Takes in a role, the emoji for react role and message ID which to be reacted to in order to get this role assigned
        """
        print(emoji)
        reactRole = Role.Role(
            roleId = role.id,
            roleEmoji = emoji,
            assignedTo = [],
            messageId = messageId
        )

        message = await ctx.channel.fetch_message(messageId)
        await message.add_reaction(str(emoji))

        reactRole.save()
        await ctx.send('Saved')


def setup(bot: commands.Bot):
    bot.add_cog(Roles(bot))