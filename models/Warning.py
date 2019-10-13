from mongoengine import Document, StringField, LongField, IntField, BooleanField

class Warning(Document):
    reason = StringField(required=True)
    warnedAt = IntField(required=True)
    warnedById = LongField(required=True)
    warnedByUsername = StringField(required=True)
    warnedUserId = LongField(required=True)
    warnedUserUsername = StringField(required=True)