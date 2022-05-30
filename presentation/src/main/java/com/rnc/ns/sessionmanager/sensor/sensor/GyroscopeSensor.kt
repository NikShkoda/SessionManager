package com.rnc.ns.sessionmanager.sensor.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.rnc.ns.sessionmanager.sensor.filter.OrientationGyroscope
import com.rnc.ns.sessionmanager.sensor.util.rotation.RotationUtil.getOrientationVectorFromAccelerationMagnetic
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScoped
class GyroscopeSensor @Inject constructor(
    @ActivityContext context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val listener = SimpleSensorListener()
    private var magnetic = FloatArray(3)
    private var acceleration = FloatArray(3)
    private var rotation = FloatArray(3)
    private var initialZRotation = 0.0
    private var lastZRotation = 0.0
    private var sensorDelay = DEFAULT_SENSOR_DELAY
    private val sensorType = Sensor.TYPE_GYROSCOPE

    private val _sensorFlow: MutableSharedFlow<Double> = MutableSharedFlow()
    val sensorFlow
        get() = _sensorFlow

    fun start() {
        registerSensors(sensorDelay)
    }

    fun stop() {
        unregisterSensors()
    }

    fun setSensorDelay(sensorDelay: Int) {
        this.sensorDelay = sensorDelay
    }

    fun reset() {
        stop()
        magnetic = FloatArray(3)
        acceleration = FloatArray(3)
        rotation = FloatArray(3)
        listener.reset()
        start()
    }

    private fun processAcceleration(rawAcceleration: FloatArray) {
        System.arraycopy(rawAcceleration, 0, acceleration, 0, acceleration.size)
    }

    private fun processMagnetic(magnetic: FloatArray) {
        System.arraycopy(magnetic, 0, this.magnetic, 0, this.magnetic.size)
    }

    private fun processRotation(rotation: FloatArray) {
        System.arraycopy(rotation, 0, this.rotation, 0, this.rotation.size)
    }

    private fun registerSensors(sensorDelay: Int) {
        OrientationGyroscope.reset()

        sensorManager.registerListener(
            listener, sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            sensorDelay
        )

        sensorManager.registerListener(
            listener, sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            sensorDelay
        )

        sensorManager.registerListener(
            listener,
            sensorManager.getDefaultSensor(sensorType),
            sensorDelay
        )
    }

    private fun unregisterSensors() {
        sensorManager.unregisterListener(listener)
    }

    /**
     * Only update [_sensorFlow] in following cases:
     * [lastZRotation] is not set, which means device is in default position,
     * and delta rotation become equal or less than -30 or equal or more than 30
     * [lastZRotation] is equal or less than -30 and delta is more that that
     * [lastZRotation] is equal or more than 30 and delta is less that that
     * [lastZRotation] is between -30 and 30 exclusive and delta is
     * equal or less than -30 or equal or more than 30
     */
    private fun setOutput(value: FloatArray) {
        val zRotation = (Math.toDegrees(value[0].toDouble()) + 360) % 360
        if(initialZRotation == 0.0 && zRotation != 0.0) {
            initialZRotation = zRotation
        }
        val rotationDelta = zRotation - initialZRotation
        if(lastZRotation == 0.0
            && (rotationDelta <= -ANGLE || rotationDelta >= ANGLE)) {
            lastZRotation = rotationDelta
            coroutineScope.launch {
                _sensorFlow.emit(rotationDelta)
            }
        } else if(lastZRotation <= -ANGLE && rotationDelta > -ANGLE
            || lastZRotation >= ANGLE && rotationDelta < ANGLE
            || ((lastZRotation > -ANGLE && lastZRotation < ANGLE)
                    && (rotationDelta <= -ANGLE || rotationDelta >= ANGLE))) {
            coroutineScope.launch {
                _sensorFlow.emit(rotationDelta)
            }
            lastZRotation = rotationDelta
        }
    }

    private inner class SimpleSensorListener : SensorEventListener {
        private var hasAcceleration = false
        private var hasMagnetic = false
        fun reset() {
            hasAcceleration = false
            hasMagnetic = false
        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                processAcceleration(event.values)
                hasAcceleration = true
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                processMagnetic(event.values)
                hasMagnetic = true
            } else if (event.sensor.type == sensorType) {
                processRotation(event.values)
                if (!OrientationGyroscope.isBaseOrientationSet) {
                    if (hasAcceleration && hasMagnetic) {
                        OrientationGyroscope.setBaseOrientation(
                            getOrientationVectorFromAccelerationMagnetic(acceleration, magnetic)
                        )
                    }
                } else {
                    setOutput(
                        OrientationGyroscope.calculateOrientation(
                            rotation,
                            event.timestamp
                        )
                    )
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    companion object {
        private val TAG = GyroscopeSensor::class.java.simpleName
        const val ANGLE = 30
        const val DEFAULT_SENSOR_DELAY = 100000
    }
}