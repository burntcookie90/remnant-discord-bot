package com.vishnurajeevan

import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.string

suspend fun Kord.ring() {
  createGlobalChatInputCommand(
    name = "ring",
    description = "find remnant ring"
  ) {
    string(QUERY, "what to search for") {
      required = true
    }
  }
}