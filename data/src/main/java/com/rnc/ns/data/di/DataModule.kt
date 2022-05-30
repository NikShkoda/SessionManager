package com.rnc.ns.data.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rnc.ns.data.database.SessionManagerDatabase
import com.rnc.ns.data.database.dao.SessionDao
import com.rnc.ns.data.database.model.SessionEntity
import com.rnc.ns.data.datasource.SessionDbDataSource
import com.rnc.ns.data.repository.SessionRepositoryImpl
import com.rnc.ns.domain.repository.SessionRepository
import com.rnc.ns.domain.usecase.GetSessionUseCase
import com.rnc.ns.domain.usecase.IncrementSessionCountUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Singleton
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideRealmDatabase(application: Application): SessionManagerDatabase {
        return Room.databaseBuilder(
            application,
            SessionManagerDatabase::class.java,
            "session-manager"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSessionDao(
        database: SessionManagerDatabase,
        coroutineScope: CoroutineScope,
    ): SessionDao {
        return database.sessionDao().apply {
            coroutineScope.launch {
                insertSession(
                    SessionEntity(
                        sessionCount = 1,
                        lastSessionTime = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    @Provides
    @Singleton
    fun provideSessionDbDataSource(sessionDao: SessionDao): SessionDbDataSource {
        return SessionDbDataSource(sessionDao)
    }

    @Provides
    @Singleton
    fun provideSessionRepository(sessionDbDataSource: SessionDbDataSource): SessionRepository {
        return SessionRepositoryImpl(sessionDbDataSource)
    }

    @Provides
    @Singleton
    fun provideGetSessionUseCase(sessionRepository: SessionRepository): GetSessionUseCase {
        return GetSessionUseCase(sessionRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateSessionUseCase(sessionRepository: SessionRepository): IncrementSessionCountUseCase {
        return IncrementSessionCountUseCase(sessionRepository)
    }
}