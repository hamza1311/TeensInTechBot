from mongoengine import Document, StringField, LongField, ListField

class Role(Document):
    roleId = LongField(required=True)
    roleEmoji = StringField(required=True)
    assignedTo = ListField()
    messageId = LongField(required=True)