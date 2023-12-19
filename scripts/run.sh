#!/bin/bash
mkdir -p run
mkdir -p data
server/build/install/remnant-discord-bot/bin/remnant-discord-bot run \
    --token "${DISCORD_TOKEN}"
