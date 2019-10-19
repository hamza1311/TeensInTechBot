from mongoengine import Document, StringField, LongField, IntField

class Kick(Document):
    reason = StringField(required=False)
    kickedAt = IntField(required=True)
    kickedById = LongField(required=True)
    kickedByUsername = StringField(required=True)
    kickedUserId = LongField(required=True)
    kickedUserUsername = StringField(required=True)