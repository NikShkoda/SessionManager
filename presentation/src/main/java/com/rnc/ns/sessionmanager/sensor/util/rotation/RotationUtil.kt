package com.rnc.ns.sessionmanager.sensor.util.rotation

import android.hardware.SensorManager
import android.renderscript.Matrix3f
import org.apache.commons.math3.complex.Quaternion
import kotlin.math.pow
import kotlin.math.sqrt

object RotationUtil {
    /**
     * Calculates a rotation vector from the gyroscope angular speed values.
     *
     *
     * http://developer.android.com/reference/android/hardware/SensorEvent.html#values
     *
     * @param previousRotationVector the last known orientation to which the new rotation will be applied.
     * @param rateOfRotation         the rotation measurement
     * @param dt                     the period of time over which the rotation measurement took place in units of seconds
     * @param epsilon                minimum rotation vector magnitude required to get the axis for normalization
     * @return A Quaternion representing the orientation.
     */
    @JvmStatic
    fun integrateGyroscopeRotation(
        previousRotationVector: Quaternion,
        rateOfRotation: FloatArray,
        dt: Float,
        epsilon: Float
    ): Quaternion {
        // Calculate the angular speed of the sample
        val magnitude = sqrt(
            rateOfRotation[0].toDouble().pow(2.0) +
                    rateOfRotation[1].toDouble().pow(2.0) +
                    rateOfRotation[2].toDouble().pow(2.0)
        ).toFloat()

        // Normalize the rotation vector if it's big enough to get the axis
        if (magnitude > epsilon) {
            rateOfRotation[0] /= magnitude
            rateOfRotation[1] /= magnitude
            rateOfRotation[2] /= magnitude
        }

        // Integrate around this axis with the angular speed by the timestep
        // in order to get a delta rotation from this sample over the timestep
        // We will convert this axis-angle representation of the delta rotation
        // into a quaternion before turning it into the rotation matrix.
        val thetaOverTwo = magnitude * dt / 2.0f
        val sinThetaOverTwo = Math.sin(thetaOverTwo.toDouble()).toFloat()
        val cosThetaOverTwo = Math.cos(thetaOverTwo.toDouble()).toFloat()
        val deltaVector = DoubleArray(4)
        deltaVector[0] = (sinThetaOverTwo * rateOfRotation[0]).toDouble()
        deltaVector[1] = (sinThetaOverTwo * rateOfRotation[1]).toDouble()
        deltaVector[2] = (sinThetaOverTwo * rateOfRotation[2]).toDouble()
        deltaVector[3] = cosThetaOverTwo.toDouble()

        // Since it is a unit quaternion, we can just multiply the old rotation
        // by the new rotation delta to integrate the rotation.
        return previousRotationVector.multiply(
            Quaternion(
                deltaVector[3], deltaVector.copyOfRange(0, 3)
            )
        )
    }

    /**
     * Calculates orientation vector from accelerometer and magnetometer output.
     *
     * @param acceleration the acceleration measurement.
     * @param magnetic     the magnetic measurement.
     * @return
     */
    @JvmStatic
    fun getOrientationVectorFromAccelerationMagnetic(
        acceleration: FloatArray?,
        magnetic: FloatArray?
    ): Quaternion? {
        val rotationMatrix = FloatArray(9)
        if (SensorManager.getRotationMatrix(rotationMatrix, null, acceleration, magnetic)) {
            val rotation = getQuaternion(Matrix3f(rotationMatrix))
            return Quaternion(rotation[0], rotation[1], rotation[2], rotation[3])
        }
        return null
    }

    @Suppress("DEPRECATION")
    private fun getQuaternion(m1: Matrix3f): DoubleArray {
        val w = sqrt(1.0 + m1[0, 0] + m1[1, 1] + m1[2, 2]) / 2.0
        val w4 = 4.0 * w
        val x = (m1[2, 1] - m1[1, 2]) / w4
        val y = (m1[0, 2] - m1[2, 0]) / w4
        val z = (m1[1, 0] - m1[0, 1]) / w4
        return doubleArrayOf(w, x, y, z)
    }
}