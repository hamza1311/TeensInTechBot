from discord import Color
from random import randint

def randomDiscordColor() -> Color:
    return Color(value=randint(0, 16777215))