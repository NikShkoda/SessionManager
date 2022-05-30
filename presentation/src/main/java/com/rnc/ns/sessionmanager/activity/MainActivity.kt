package com.rnc.ns.sessionmanager.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.rnc.ns.sessionmanager.sensor.sensor.GyroscopeSensor
import com.rnc.ns.sessionmanager.ui.SessionManagerUI
import com.rnc.ns.sessionmanager.ui.theme.SessionManagerTheme
import com.rnc.ns.sessionmanager.viewmodel.SessionManagerViewModel
import com.rnc.ns.sessionmanager.worker.SessionCounterWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var gyroscopeSensor: GyroscopeSensor
    private val viewModel: SessionManagerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SessionManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SessionManagerUI(viewModel = viewModel)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initSensor()
        cancelWork()
    }

    override fun onStop() {
        super.onStop()
        stopSensor()
        startWork()
    }

    private fun initSensor() {
        gyroscopeSensor.start()
        viewModel.startSensorUpdates(gyroscopeSensor.sensorFlow)
    }

    private fun cancelWork() {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(SessionCounterWorker.TAG)
    }

    private fun stopSensor() {
        gyroscopeSensor.stop()
    }

    private fun startWork() {
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            SessionCounterWorker.TAG,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.Builder(SessionCounterWorker::class.java)
                .setInitialDelay(SessionCounterWorker.DELAY, TimeUnit.MINUTES)
                .build()
        )
    }
 }