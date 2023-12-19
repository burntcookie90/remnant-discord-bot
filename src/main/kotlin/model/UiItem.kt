package com.vishnurajeevan.model

import kotlinx.serialization.Serializable

@Serializable
data class UiItem(
  val name: String,
  val description: String,
  val iconURL: String,
  val linkURL: String,
  val tags: String
)
