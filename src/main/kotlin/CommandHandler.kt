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
    val query = command.strings[QUERY]!!.replace(" ", "+")
    response.respond {
      embed {
        title = ":white_check_mark: Found:"
        description = "Here's what we found!"
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
          val findRingsByName =
            when (interaction.command.rootName) {
              "ring" -> db.itemQueries.findRingsByName(query, mapper)
              "amulet" -> db.itemQueries.findAmuletsByName(query, mapper)
              "mod" -> db.itemQueries.findModByName(query, mapper)
              "mutator" -> db.itemQueries.findMutatorByName(query, mapper)
              "remnant" -> db.itemQueries.findItemByName(query, mapper)
              else -> error("unhandled!")
            }
          val items = findRingsByName
            .asFlow()
            .mapToList(Dispatchers.IO)
            .first()
          if (items.isEmpty()) {
            field {
              name = "None found!"
            }
          }
          else {
            items
              .forEach {
                field {
                  name = it.name
                  value = "${it.linkURL}\n${it.description}\ntags: [${it.tags}]"
                }
              }
          }
      }
    }
  }
}