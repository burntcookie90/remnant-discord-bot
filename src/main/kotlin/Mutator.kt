package com.vishnurajeevan

import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.string

suspend fun Kord.mutator() {
  createGlobalChatInputCommand(
    name = "mutator",
    description = "find remnant mutator"
  ) {
    string("query", "what to search for") {
      required = true
    }
  }
}