package com.rnc.ns.sessionmanager.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rnc.ns.domain.usecase.IncrementSessionCountUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SessionCounterWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    val incrementSessionCountUseCase: IncrementSessionCountUseCase
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        incrementSessionCountUseCase()
        return Result.success()
    }

    companion object {
        const val TAG = "SessionCounterWorker"
        const val DELAY = 10L
    }
}
