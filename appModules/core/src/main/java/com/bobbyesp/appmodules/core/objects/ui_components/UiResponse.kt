package com.bobbyesp.appmodules.core.objects.ui_components

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UiResponse (
  val title: String? = null,
  val header: UiItem? = null,
  val body: List<UiItem>,
  val id: String? = null, // album-entity-view
)