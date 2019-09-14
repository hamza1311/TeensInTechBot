FROM openjdk:10-jre

RUN mkdir - p /usr/src/bot

WORKDIR /usr/src/bot

COPY . /usr/src/bot

ENV BOT_TOKEN=NTM1NzUwMzQ0ODU3MzU0MjQx.XXO9Cg.2G79gyg9Db1jCywXB7uujkWzm94

EXPOSE 443

RUN ./gradlew run
