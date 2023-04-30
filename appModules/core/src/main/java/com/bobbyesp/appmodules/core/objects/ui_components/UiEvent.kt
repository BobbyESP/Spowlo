package com.bobbyesp.appmodules.core.objects.ui_components


import com.bobbyesp.appmodules.core.objects.player.PlayFromContextData
import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.DefaultObject
import dev.zacsweers.moshix.sealed.annotations.TypeLabel

@JsonClass(generateAdapter = true, generator = "sealed:name")
sealed class UiEvent {
  @JsonClass(generateAdapter = true)
  @TypeLabel("navigate")
  class NavigateToUri (
    val data: NavigateUri
  ): UiEvent()

  @JsonClass(generateAdapter = true)
  @TypeLabel("playFromContext")
  class PlayFromContext (
    val data: PlayFromContextData
  ): UiEvent()

  @DefaultObject
  object Unknown: UiEvent()
}