package com.bobbyesp.spowlo.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.bobbyesp.spowlo.db.dao.common.BaseDao
import com.bobbyesp.spowlo.db.entity.format.FormatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormatDao: BaseDao<FormatEntity> {
    @Query("SELECT * FROM format WHERE id = :id")
    fun format(id: String?): Flow<FormatEntity?>
}