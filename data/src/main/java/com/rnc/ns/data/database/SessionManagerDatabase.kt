package com.rnc.ns.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rnc.ns.data.database.dao.SessionDao
import com.rnc.ns.data.database.model.SessionEntity

@Database(entities = [SessionEntity::class], version = 1)
abstract class SessionManagerDatabase: RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}