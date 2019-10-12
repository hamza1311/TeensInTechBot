from mongoengine import Document, StringField, LongField

class Save(Document):
    data = StringField(required=True)
    savedBy = LongField(required=True)