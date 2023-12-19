#!/bin/sh
mkdir -p run
mkdir -p data
bin/remnant-discord-bot run --token "${DISCORD_TOKEN}"
