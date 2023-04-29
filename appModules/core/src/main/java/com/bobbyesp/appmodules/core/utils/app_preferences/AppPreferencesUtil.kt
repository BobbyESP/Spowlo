package com.bobbyesp.appmodules.core.utils.app_preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferencesUtil {
    object AppPreferencesUtil {
        private var sharedPreferences: SharedPreferences? = null

        fun setupConfig(context: Context) {
            sharedPreferences = context.getSharedPreferences("jetispot.appconfig",
                Context.MODE_PRIVATE
            )
        }

        /*  var Setting1: Float?
              get() = Key./*Get the key*/./*function*/
              set(value) = /*Same*/
      */

        private enum class Key {
            Setting1;

            fun getBoolean(defValue: Boolean = false, elseValue: Boolean? = null): Boolean? = if (sharedPreferences?.contains(name) == true) sharedPreferences!!.getBoolean(name, defValue) else elseValue
            fun getFloat(defValue: Float = 0f, elseValue: Float? = null): Float? = if (sharedPreferences?.contains(name) == true) sharedPreferences!!.getFloat(name, defValue) else elseValue
            fun getInt(defValue: Int = 0, elseValue: Int? = null): Int? = if (sharedPreferences?.contains(name) == true) sharedPreferences!!.getInt(name, defValue) else elseValue
            fun getLong(defValue: Long = 0, elseValue: Long? = null): Long? = if (sharedPreferences?.contains(name) == true) sharedPreferences!!.getLong(name, defValue) else elseValue
            fun getString(defValue: String = "", elseValue: String? = null): String? = if (sharedPreferences?.contains(name) == true) sharedPreferences!!.getString(name, defValue) else elseValue

            fun setBoolean(value: Boolean?) = value?.let { sharedPreferences!!.edit { putBoolean(name, value) } } ?: remove()
            fun setFloat(value: Float?) = value?.let { sharedPreferences!!.edit { putFloat(name, value) } } ?: remove()
            fun setInt(value: Int?) = value?.let { sharedPreferences!!.edit { putInt(name, value) } } ?: remove()
            fun setLong(value: Long?) = value?.let { sharedPreferences!!.edit { putLong(name, value) } } ?: remove()
            fun setString(value: String?) = value?.let { sharedPreferences!!.edit { putString(name, value) } } ?: remove()

            fun remove() = sharedPreferences!!.edit { remove(name) }
        }
    }
}