package com.bobbyesp.spowlo.db

import androidx.room.TypeConverter
import com.bobbyesp.utilities.utilities.toLocalDateTime
import com.bobbyesp.utilities.utilities.toLong
import kotlinx.datetime.LocalDateTime

class DbConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? =
        value?.toLocalDateTime()

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? =
        date?.toLong()
}
