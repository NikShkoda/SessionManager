package com.rnc.ns.sessionmanager.sensor.filter

import com.rnc.ns.sessionmanager.sensor.util.rotation.RotationUtil.integrateGyroscopeRotation
import com.rnc.ns.sessionmanager.sensor.util.angle.AngleUtils.getAngles
import org.apache.commons.math3.complex.Quaternion
import java.lang.IllegalStateException

/**
 * OrientationComplimentaryFilter estimates the orientation of the devices based on a sensor fusion of a
 * gyroscope, accelerometer and magnetometer. The fusedOrientation is backed by a quaternion based complimentary fusedOrientation.
 *
 *
 * The complementary fusedOrientation is a frequency domain fusedOrientation. In its strictest
 * sense, the definition of a complementary fusedOrientation refers to the use of two or
 * more transfer functions, which are mathematical complements of one another.
 * Thus, if the data from one sensor is operated on by G(s), then the data from
 * the other sensor is operated on by I-G(s), and the sum of the transfer
 * functions is I, the identity matrix.
 *
 *
 * OrientationComplimentaryFilter attempts to fuse magnetometer, gravity and gyroscope
 * sensors together to produce an accurate measurement of the rotation of the
 * device.
 *
 *
 * The magnetometer and acceleration sensors are used to determine one of the
 * two orientation estimations of the device. This measurement is subject to the
 * constraint that the device must not be accelerating and hard and soft-iron
 * distortions are not present in the local magnetic field..
 *
 *
 * The gyroscope is used to determine the second of two orientation estimations
 * of the device. The gyroscope can have a shorter response time and is not
 * effected by linear acceleration or magnetic field distortions, however it
 * experiences drift and has to be compensated periodically by the
 * acceleration/magnetic sensors to remain accurate.
 *
 *
 * Quaternions are used to integrate the measurements of the gyroscope and apply
 * the rotations to each sensors measurements via complementary fusedOrientation. This the
 * ideal method because quaternions are not subject to many of the singularties
 * of rotation matrices, such as gimbal lock.
 *
 *
 * The quaternion for the magnetic/acceleration sensor is only needed to apply
 * the weighted quaternion to the gyroscopes weighted quaternion via
 * complementary fusedOrientation to produce the fused rotation. No integrations are
 * required.
 *
 *
 * The gyroscope provides the angular rotation speeds for all three axes. To
 * find the orientation of the device, the rotation speeds must be integrated
 * over time. This can be accomplished by multiplying the angular speeds by the
 * time intervals between sensor updates. The calculation produces the rotation
 * increment. Integrating these values again produces the absolute orientation
 * of the device. Small errors are produced at each iteration causing the gyro
 * to drift away from the true orientation.
 *
 *
 * To eliminate both the drift and noise from the orientation, the gyroscope
 * measurements are applied only for orientation changes in short time
 * intervals. The magnetometer/acceleration fusion is used for long time
 * intervals. This is equivalent to low-pass filtering of the accelerometer and
 * magnetic field sensor signals and high-pass filtering of the gyroscope
 * signals.
 *
 * @author Kaleb
 * http://developer.android.com/reference/android/hardware/SensorEvent.html#values
 */
object OrientationGyroscope {
    private var rotationVectorGyroscope: Quaternion? = null
    private var timestamp: Long = 0
    private val TAG = OrientationGyroscope::class.java.simpleName
    private const val NS2S = 1.0f / 1000000000.0f
    private const val EPSILON = 0.000000001f
    var output: FloatArray = FloatArray(3)

    /**
     * Calculate the fused orientation of the device.
     *
     * Rotation is positive in the counterclockwise direction (right-hand rule). That is, an observer looking from some positive location on the x, y, or z axis at
     * a device positioned on the origin would report positive rotation if the device appeared to be rotating counter clockwise. Note that this is the
     * standard mathematical definition of positive rotation and does not agree with the aerospace definition of roll.
     *
     * See: https://source.android.com/devices/sensors/sensor-types#rotation_vector
     *
     * Returns a vector of size 3 ordered as:
     * [0]X points east and is tangential to the ground.
     * [1]Y points north and is tangential to the ground.
     * [2]Z points towards the sky and is perpendicular to the ground.
     *
     * @param gyroscope the gyroscope measurements.
     * @param timestamp the gyroscope timestamp
     * @return An orientation vector -> @link SensorManager#getOrientation(float[], float[])}
     */
    fun calculateOrientation(gyroscope: FloatArray, timestamp: Long): FloatArray {
        return if (isBaseOrientationSet) {
            rotationVectorGyroscope?.let {
                if (this.timestamp != 0L) {
                    val dT = (timestamp - this.timestamp) * NS2S
                    rotationVectorGyroscope = integrateGyroscopeRotation(
                        previousRotationVector = it,
                        rateOfRotation = gyroscope,
                        dt = dT,
                        epsilon = EPSILON
                    ).also { rotationVectorGyroscope ->
                        output = getAngles(
                            rotationVectorGyroscope.q0,
                            rotationVectorGyroscope.q1,
                            rotationVectorGyroscope.q2,
                            rotationVectorGyroscope.q3
                        )
                    }
                }
            }
            this.timestamp = timestamp
            output
        } else {
            throw IllegalStateException("You must call setBaseOrientation() before calling calculateFusedOrientation()!")
        }
    }

    /**
     * Set the base orientation (frame of reference) to which all subsequent rotations will be applied.
     *
     *
     * To initialize to an arbitrary local frame of reference pass in the Identity Quaternion. This will initialize the base orientation as the orientation the device is
     * currently in and all subsequent rotations will be relative to this orientation.
     *
     *
     * To initialize to an absolute frame of reference (like Earth frame) the devices orientation must be determine from other sensors (such as the acceleration and magnetic
     * sensors).
     * @param baseOrientation The base orientation to which all subsequent rotations will be applied.
     */
    fun setBaseOrientation(baseOrientation: Quaternion?) {
        rotationVectorGyroscope = baseOrientation
    }

    fun reset() {
        rotationVectorGyroscope = null
        timestamp = 0
    }

    val isBaseOrientationSet: Boolean
        get() = rotationVectorGyroscope != null
}