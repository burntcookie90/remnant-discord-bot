package com.vishnurajeevan

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.vishnurajeevan.model.UiItem
import com.vishnurajeevan.remnant.Database
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.message.embed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first

suspend fun Kord.commandHandler(db: Database) {
  on<ChatInputCommandInteractionCreateEvent> {
    val response = interaction.deferPublicResponse()
    val command = interaction.command
    val query = "\"${command.strings[QUERY]!!.replace(" ", "+")}\""
    val mapper: (name: String, description: String, iconURL: String?, linkURL: String, tags: String?) -> UiItem =
      { name, description, iconURL, linkURL, tags ->
        UiItem(
          name,
          description,
          iconURL.orEmpty(),
          linkURL,
          tags.orEmpty()
        )
      }
    val items =
      when (interaction.command.rootName) {
        "ring" -> db.itemQueries.findRingsByName(query, mapper)
        "amulet" -> db.itemQueries.findAmuletsByName(query, mapper)
        "mod" -> db.itemQueries.findModByName(query, mapper)
        "mutator" -> db.itemQueries.findMutatorByName(query, mapper)
        "relic" -> db.itemQueries.findRelicByName(query, mapper)
        "remnant" -> db.itemQueries.findItemByName(query, mapper)
        else -> error("unhandled!")
      }
        .asFlow()
        .mapToList(Dispatchers.IO)
        .first()
    response.respond {
      embed {
        if (items.isEmpty()) {
          title = ":no_entry: None found!"
        }
        else if (items.size > 20) {
          title = ":warning: Too many results"
          description = "Reduce search scope!"
        }
        else {
          title = ":white_check_mark: Found:"
          description = "Here's what we found!"
          items
            .forEach {
              val emoji = when {
                it.tags.lowercase().contains("ring") -> ":ring:"
                it.tags.lowercase().contains("amulet") -> ":prayer_beads:"
                it.tags.lowercase().contains("mod") -> ":gear:"
                it.tags.lowercase().contains("mutator") -> ":electric_plug:"
                it.tags.lowercase().contains("relic") -> ":anatomical_heart:"
                else -> ""
              }
              field {
                name = it.name
                value = "$emoji [wiki](${it.linkURL})\n\n${it.description}\n\ntags: [${it.tags}]"
                inline = true
              }
            }
        }
      }
    }
  }
}