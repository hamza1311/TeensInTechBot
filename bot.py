import discord
from discord.ext import commands
from util.functions import randomDiscordColor, isMod # pylint: disable=no-name-in-module
import models
from util.publicCommads import publicCommands # pylint: disable=no-name-in-module


class Bot(commands.Cog):
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

    
    @commands.command()
    @commands.is_owner()
    async def reload(self, ctx: commands.Context, module: str):
        """
        Reloads a cog
        """

        self.bot.reload_extension(f'cogs.{module}')
        await ctx.send("🔄")

    @commands.command()
    async def help(self, ctx: commands.Context, command: str = None):
        """
        Displays help message
        """

        embed = discord.Embed(title='**You wanted help? Help is provided**', color = randomDiscordColor())
        embed.set_footer(text=f'Do {self.bot.command_prefix}help commndName to get help for a specific command')

        if command is None:
            print(publicCommands)

            for name, cog in self.bot.cogs.items():
                out = ''
                cmds = [x for x in cog.get_commands() if x.name in publicCommands or (await self.bot.is_owner(ctx.author)) or isMod(ctx)]
                for cmd in cmds:
                    helpStr = str(cmd.help).split('\n')[0]
                    out += f"**{cmd.name}**:\t{helpStr}\n"

                embed.add_field(name=f'**{name}**', value=out, inline=False)
            
            # out = ''
            # cmds = [help, reload]
            # for cmd in cmds:
            #     helpStr = str(cmd.help).split('\n')[0]
            #     out += f"**{cmd.name}**:\t{helpStr}\n"

            # embed.add_field(name='**Uncategorized**', value=out, inline=False)
        else:
            
            cmd = self.bot.get_command(command)

            if cmd is None:
                await ctx.send(f"Command {command} doesn't exist")
                return

            embed.add_field(name = f'{cmd.name}', value = cmd.help, inline=False)

            cmdAliases = cmd.aliases
            if (cmdAliases):
                aliases = ''
                for i in range(0, len(cmdAliases)):
                    aliases += f'{i + 1}. {cmdAliases[i]}\n'

                embed.add_field(name='Aliases:', value=aliases, inline=False)
            
        await ctx.send(embed=embed)


def setup(bot: commands.Bot):
    bot.add_cog(Bot(bot))