package com.vishnurajeevan

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.vishnurajeevan.model.ApiItem
import com.vishnurajeevan.remnant.Database
import dev.kord.core.Kord
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

const val QUERY = "query"


fun main(args: Array<String>) {
  NoOpCliktCommand(name = "remnant-discord-bot")
    .subcommands(Run())
    .main(args)
}

suspend inline fun withDatabase(path: String, crossinline block: suspend (db: Database) -> Unit) {
  JdbcSqliteDriver("jdbc:sqlite:$path").use { driver ->
    try {
      Database.Schema.create(driver)
    } catch (e: Exception) {
    }
    block(Database(driver))
  }
}

private abstract class DiscordCommand(
  name: String,
  help: String,
) : CliktCommand(
  name = name,
  help = help
) {
  private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  protected val token by option("--token").required()

  override fun run() =
    runBlocking {
      withDatabase("data/data.db") { db ->
        run(db)
      }
    }

  abstract suspend fun run(db: Database)
}

private class Run : DiscordCommand("run", "start server") {
  @OptIn(ExperimentalSerializationApi::class)
  override suspend fun run(db: Database) {
    if (db.itemQueries.itemCount().asFlow().mapToOne(Dispatchers.IO).first() == 0L) {
      val items = Json.decodeFromStream<List<ApiItem>>(ApiItem::class.java.classLoader.getResourceAsStream("db.json")!!)
      db.transaction {
        items.forEach {
          db.itemQueries.insertItem(
            name = it.name,
            description = it.description,
            iconURL = it.iconURL,
            linkURL = it.linkURL,
            tags = it.tags.joinToString(",")
          )
        }
      }
    }

    with(Kord(token= token)) {
      ring()
      amulet()
      mod()
      mutator()
      remnant()
      commandHandler(db)
      login {
        // we need to specify this to receive the content of messages
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
      }
    }

  }
}