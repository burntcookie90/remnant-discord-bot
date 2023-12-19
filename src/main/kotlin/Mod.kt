package com.vishnurajeevan

import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.string

suspend fun Kord.mod() {
  createGlobalChatInputCommand(
    name = "mod",
    description = "find remnant amulet"
  ) {
    string("query", "what to search for") {
      required = true
    }
  }
}