package com.bobbyesp.spotdl_utilities.preferences

import com.tencent.mmkv.MMKV

const val PYTHON_VERSION = "python_version"
const val FFMPEG_VERSION = "ffmpeg_version"

private val StringPreferenceDefaults: Map<String, String> =
    mapOf(
      PYTHON_VERSION to "3.11.0",
        FFMPEG_VERSION to "5.1"
    )

object PreferencesUtil {
    private val kv: MMKV = MMKV.defaultMMKV()

//    fun String.getInt(default: Int = IntPreferenceDefaults.getOrElse(this) { 0 }): Int =
//        kv.decodeInt(this, default)

    fun String.getString(default: String = StringPreferenceDefaults.getOrElse(this) { "" }): String =
        kv.decodeString(this) ?: default

//    fun String.getBoolean(default: Boolean = BooleanPreferenceDefaults.getOrElse(this) { false }): Boolean =
//        kv.decodeBool(this, default)

    fun String.updateString(newString: String) = kv.encode(this, newString)
    fun String.updateInt(newInt: Int) = kv.encode(this, newInt)
    fun String.updateBoolean(newValue: Boolean) = kv.encode(this, newValue)
    fun updateValue(key: String, b: Boolean) = key.updateBoolean(b)
    fun encodeInt(key: String, int: Int) = key.updateInt(int)

//    fun getValue(key: String): Boolean = key.getBoolean()
    fun encodeString(key: String, string: String) = key.updateString(string)
    fun containsKey(key: String) = kv.containsKey(key)

}