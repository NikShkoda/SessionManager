package com.rnc.ns.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SessionEntity(
    @PrimaryKey
    val id: Long = 1,
    val sessionCount: Int,
    val lastSessionTime: Long
)