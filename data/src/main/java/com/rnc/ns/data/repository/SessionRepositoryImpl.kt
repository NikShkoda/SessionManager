package com.rnc.ns.data.repository

import com.rnc.ns.data.datasource.SessionDbDataSource
import com.rnc.ns.domain.model.Session
import com.rnc.ns.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionRepositoryImpl(
    private val sessionDbDataSource: SessionDbDataSource
): SessionRepository {
    override suspend fun getSession(): Flow<Session> {
        return sessionDbDataSource.getSession().map { entity ->
            Session(
                sessionCount = entity.sessionCount,
                lastSessionTime = entity.lastSessionTime
            )
        }
    }

    override suspend fun incrementSessionCount() {
        sessionDbDataSource.incrementSessionCount()
    }
}