from mongoengine import Document, StringField, LongField, IntField, ListField

class BotConfig(Document):
    serverId = LongField(required=True, primary_key=True)
    rolesChannelId = LongField(required=True)
    modIds = ListField(required=True)
    welcomeChannel = LongField(required=True)

    @staticmethod
    def getForGuild(guildId):
        return BotConfig.objects(serverId=guildId).get() # pylint: disable=no-member
