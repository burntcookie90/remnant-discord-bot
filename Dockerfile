FROM --platform=linux/amd64 eclipse-temurin:17-alpine AS build
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dkotlin.incremental=false"
WORKDIR /app

COPY gradlew settings.gradle.kts ./
COPY gradle ./gradle
RUN ./gradlew --version

COPY build.gradle.kts ./
COPY src ./src

RUN ./gradlew installDist

FROM bfren/alpine-s6:alpine3.18.5-5.1.0
LABEL maintainer="Vishnu Rajeevan <github@vishnu.email>"

RUN apk add --no-cache \
      curl \
      openjdk17-jre \
 && rm -rf /var/cache/* \
 && mkdir /var/cache/apk

ENV \
    # Fail if cont-init scripts exit with non-zero code.
    S6_BEHAVIOUR_IF_STAGE2_FAILS=2 \
    PUID="" \
    PGID="" \
    DISCORD_TOKEN=""

COPY root/cont-init.d /etc/
COPY root/services.d /etc/
WORKDIR /app
COPY --from=build /app/build/install/remnant-discord-bot ./
