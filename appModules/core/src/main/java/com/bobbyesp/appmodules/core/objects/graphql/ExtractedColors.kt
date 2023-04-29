package com.bobbyesp.appmodules.core.objects.graphql

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ExtractedColors(
  val extractedColors: List<ExtractedColorsResponse>
)

@JsonClass(generateAdapter = true)
class ExtractedColorsResponse(
  val colorRaw: ExtractedColor,
  val colorLight: ExtractedColor,
  val colorDark: ExtractedColor,
)

@JsonClass(generateAdapter = true)
class ExtractedColor(
  val hex: String,
  val isFallback: Boolean
)