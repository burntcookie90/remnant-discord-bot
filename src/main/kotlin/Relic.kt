package com.vishnurajeevan

import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.string

suspend fun Kord.relic() {
  createGlobalChatInputCommand(
    name = "relic",
    description = "find remnant relic"
  ) {
    string(QUERY, "what to search for") {
      required = true
    }
  }
}