package com.rnc.ns.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.rnc.ns.data.database.model.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM SessionEntity WHERE id = :id")
    fun getSessionById(id: Long): Flow<SessionEntity>

    @Query(
        "UPDATE SessionEntity " +
            "SET sessionCount = sessionCount + 1, lastSessionTime = :lastSessionTime " +
            "WHERE id = :id"
    )
    fun incrementSessionCount(id: Long, lastSessionTime: Long = System.currentTimeMillis())

    @Insert(onConflict = IGNORE)
    fun insertSession(sessionEntity: SessionEntity)
}