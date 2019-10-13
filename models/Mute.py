from mongoengine import Document, StringField, LongField, IntField, BooleanField

class Mute(Document):
    reason = StringField(required=False)
    mutedAt = IntField(required=True)
    mutedById = LongField(required=True)
    mutedByUsername = StringField(required=True)
    mutedUserId = LongField(required=True)
    mutedUserUsername = StringField(required=True)
    isStillMuted = BooleanField(required=True)