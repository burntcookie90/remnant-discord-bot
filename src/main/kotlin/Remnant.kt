package com.vishnurajeevan

import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.string

suspend fun Kord.remnant() {
  createGlobalChatInputCommand(
    name = "remnant",
    description = "find remnant anything"
  ) {
    string("query", "what to search for") {
      required = true
    }
  }
}