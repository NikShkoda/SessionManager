package com.rnc.ns.domain.repository

import com.rnc.ns.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun getSession(): Flow<Session>
    suspend fun incrementSessionCount()
}