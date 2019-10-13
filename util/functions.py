from discord import Color
from random import randint
from datetime import datetime

def randomDiscordColor() -> Color:
    return Color(value=randint(0, 16777215))

def formatTime(time: int) -> str:
    return datetime.fromtimestamp(time).strftime("%d-%m-%Y, %H:%M:%S")