package com.bobbyesp.appmodules.core.objects.graphql

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GqlWrap <T> (
  val data: T
)