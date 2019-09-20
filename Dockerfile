FROM openjdk:10-jre

RUN mkdir - p /usr/src/bot

WORKDIR /usr/src/bot

ADD build/dist/jar/TeensInTechBot-1.0-SNAPSHOT-all.jar .

ENV BOT_TOKEN=NotGonnaPushTokenAgain

CMD ["java", "-jar", "TeensInTechBot-1.0-SNAPSHOT-all.jar"]
