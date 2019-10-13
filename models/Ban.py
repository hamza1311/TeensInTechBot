from mongoengine import Document, StringField, LongField, IntField

class Ban(Document):
    reason = StringField(required=False)
    bannedById = LongField(required=True)
    bannedByUsername = StringField(required=True)
    bannedUserId = LongField(required=True)
    bannedUserUsername = StringField(required=True)
    unbanTime = IntField(required=True)
    bannedAt = IntField(required=True)

    @staticmethod
    def getTimeDelta(self) -> int:
        pass
