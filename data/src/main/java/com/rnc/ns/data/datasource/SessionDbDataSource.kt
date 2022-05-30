package com.rnc.ns.data.datasource

import com.rnc.ns.data.database.dao.SessionDao
import com.rnc.ns.data.database.model.SessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SessionDbDataSource @Inject constructor(private val sessionDao: SessionDao) {
    fun getSession(id: Long = 1): Flow<SessionEntity> = sessionDao.getSessionById(id)
    fun incrementSessionCount(id: Long = 1) = sessionDao.incrementSessionCount(id)
}