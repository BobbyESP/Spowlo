package com.bobbyesp.appmodules.core.objects.misc

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SpBlendInviteLink(
  val invite: String
)