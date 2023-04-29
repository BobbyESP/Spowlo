package com.bobbyesp.appmodules.core.utils

import com.bobbyesp.spowlo.BuildConfig

object Log {
  fun d(tag: String, message: String) = dbg { android.util.Log.d(tag(tag), message) }
  fun w(tag: String, message: String) = dbg { android.util.Log.w(tag(tag), message) }
  fun e(tag: String, message: String) = dbg { android.util.Log.e(tag(tag), message) }

  private fun tag (tag: String) = "Sp:$tag"
  private fun dbg (ifdbg: () -> Unit) = if (BuildConfig.DEBUG) ifdbg() else Unit
}