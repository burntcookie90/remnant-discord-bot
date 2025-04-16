FROM ghcr.io/puppeteer/puppeteer:latest AS scrape
WORKDIR /app
RUN git clone https://github.com/burntcookie90/remnant2browser.git
WORKDIR /app/remnant2browser
RUN yarn install
RUN npm run scrape

FROM --platform=linux/amd64 eclipse-temurin:17-alpine AS build
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dkotlin.incremental=false"
WORKDIR /app

COPY gradlew settings.gradle.kts ./
COPY gradle ./gradle
RUN ./gradlew --version

COPY build.gradle.kts ./
COPY src ./src

COPY --from=scrape /app/remnant2browser/src/data.json ./src/resources/db.json

RUN ./gradlew installDist

FROM alpine:3.19.0
LABEL maintainer="Vishnu Rajeevan <github@vishnu.email>"

RUN apk add --no-cache \
      curl \
      openjdk17-jre \
      bash \
 && rm -rf /var/cache/* \
 && mkdir /var/cache/apk

ENV \
    # Fail if cont-init scripts exit with non-zero code.
    DISCORD_TOKEN=""

WORKDIR /app
COPY scripts/run.sh ./
COPY --from=build /app/build/install/remnant-discord-bot ./

CMD /app/run.sh