package io.github.techtastic.cc_vs.util

import dan200.computercraft.api.lua.LuaException
import org.joml.*
import org.valkyrienskies.core.internal.joints.*
import kotlin.Any
import kotlin.Double
import kotlin.Pair
import kotlin.String
import kotlin.to

object CCVSUtils {
    fun Vector3dc.toLua() = mapOf(
        Pair("x", this.x()),
        Pair("y", this.y()),
        Pair("z", this.z())
    )

    fun Quaterniondc.toLua() = mapOf(
        Pair("x", this.x()),
        Pair("y", this.y()),
        Pair("z", this.z()),
        Pair("w", this.w())
    )

    fun Matrix3dc.toLua(): List<List<Double>> {
        val tensor: MutableList<List<Double>> = mutableListOf()

        for (i in 0..2) {
            val row = this.getRow(i, Vector3d())
            tensor.add(i, listOf(row.x, row.y, row.z))
        }

        return tensor
    }

    fun VSJointAndId.toLua() = mapOf(Pair("id", this.jointId), Pair("constraint", this.joint.toLua()))

    fun VSJointPose.toLua() = mapOf("pos" to this.pos.toLua(), "rot" to this.rot.toLua())

    fun VSD6Joint.LinearLimit.toLua() = mapOf(
        "extent" to this.extent.toDouble(),
        "stiffness" to this.stiffness?.toDouble(),
        "damping" to this.damping?.toDouble()
    )

    fun VSD6Joint.LinearLimitPair.toLua() = mapOf(
        "lowerLimit" to this.lowerLimit.toDouble(),
        "upperLimit" to this.upperLimit.toDouble(),
        "restitution" to this.restitution?.toDouble(),
        "bounceThreshold" to this.bounceThreshold?.toDouble(),
        "stiffness" to this.stiffness?.toDouble(),
        "damping" to this.damping?.toDouble()
    )

    fun VSD6Joint.AngularLimitPair.toLua() = mapOf(
        "lowerLimit" to this.lowerLimit.toDouble(),
        "upperLimit" to this.upperLimit.toDouble(),
        "restitution" to this.restitution?.toDouble(),
        "bounceThreshold" to this.bounceThreshold?.toDouble(),
        "stiffness" to this.stiffness?.toDouble(),
        "damping" to this.damping?.toDouble()
    )

    fun VSD6Joint.LimitCone.toLua() = mapOf(
        "yLimitAngle" to this.yLimitAngle.toDouble(),
        "zLimitAngle" to this.zLimitAngle.toDouble(),
        "restitution" to this.restitution?.toDouble(),
        "bounceThreshold" to this.bounceThreshold?.toDouble(),
        "stiffness" to this.stiffness?.toDouble(),
        "damping" to this.damping?.toDouble()
    )

    fun VSD6Joint.LimitPyramid.toLua() = mapOf(
        "yLimitAngleMin" to this.yLimitAngleMin.toDouble(),
        "yLimitAngleMax" to this.yLimitAngleMax.toDouble(),
        "zLimitAngleMin" to this.zLimitAngleMin.toDouble(),
        "zLimitAngleMax" to this.zLimitAngleMax.toDouble(),
        "restitution" to this.restitution?.toDouble(),
        "bounceThreshold" to this.bounceThreshold?.toDouble(),
        "stiffness" to this.stiffness?.toDouble(),
        "damping" to this.damping?.toDouble()
    )

    fun VSD6Joint.D6JointDrive.toLua() = mapOf(
        "driveStiffness" to this.driveStiffness,
        "driveDamping" to this.driveDamping,
        "driveForceLimit" to this.driveForceLimit,
        "isAcceleration" to this.isAcceleration
    )

    fun VSD6Joint.DrivePosition.toLua() = mapOf(
        "pose" to this.pose.toLua(),
        "autoWake" to this.autoWake
    )

    fun VSD6Joint.DriveVelocity.toLua() = mapOf(
        "linear" to this.linear[Vector3d()].toLua(),
        "angular" to this.angular[Vector3d()].toLua(),
        "autoWake" to this.autoWake
    )

    fun VSD6Joint.Hinges.toLua() = mapOf(
        "hinge0" to this.hinge0,
        "hinge1" to this.hinge1
    )

    fun VSRevoluteJoint.VSRevoluteDriveVelocity.toLua() = mapOf(
        "velocity" to this.velocity.toDouble(),
        "autoWake" to this.autoWake
    )

    fun VSRackAndPinionJoint.VSRackAndPinionJointData.toLua() = mapOf(
        "rackTeeth" to this.rackTeeth,
        "pinionTeeth" to this.pinionTeeth,
        "rackLength" to this.rackLength
    )

    fun VSJoint.toLua(): Map<String, Any?> {
        val constraint = mutableMapOf<String, Any?>()

        constraint["shipId0"] = this.shipId0
        constraint["pose0"] = this.pose0.toLua()
        constraint["shipId1"] = this.shipId1
        constraint["pose1"] = this.pose1.toLua()
        constraint["type"] = this.jointType.name
        constraint["maxForce"] = this.maxForceTorque?.maxForce?.toDouble()
        constraint["maxTorque"] = this.maxForceTorque?.maxTorque?.toDouble()

        if (this is VSDistanceJoint) {
            constraint["minDistance"] = this.minDistance?.toDouble()
            constraint["maxDistance"] = this.maxDistance?.toDouble()
            constraint["tolerance"] = this.tolerance?.toDouble()
            constraint["stiffness"] = this.stiffness?.toDouble()
            constraint["damping"] = this.damping?.toDouble()
        } else if (this is VSPrismaticJoint) {
            constraint["linearLimitPair"] = this.linearLimitPair?.toLua()
        } else if (this is VSSphericalJoint) {
            constraint["limitCone"] = this.limitCone?.toLua()
        } else if (this is VSRevoluteJoint) {
            constraint["angularLimitPair"] = this.angularLimitPair?.toLua()
            constraint["driveVelocity"] = this.driveVelocity?.toLua()
            constraint["driveForceLimit"] = this.driveForceLimit?.toDouble()
            constraint["driveGearRatio"] = this.driveGearRatio?.toDouble()
            constraint["driveFreeSpin"] = this.driveFreeSpin
        } else if (this is VSGearJoint) {
            constraint["hinges"] = this.hinges?.toLua()
            constraint["gearRatio"] = this.gearRatio?.toDouble()
        } else if (this is VSRackAndPinionJoint) {
            constraint["hinges"] = this.hinges?.toLua()
            constraint["ratio"] = this.ratio?.toDouble()
            constraint["data"] = this.data?.toLua()
        } else if (this is VSD6Joint) {
            constraint["motions"] = this.motions?.mapKeys { (axis, motion) -> axis.name }?.mapValues { (axis, motion) -> motion.name }
            constraint["distanceLimit"] = this.distanceLimit?.toLua()
            constraint["linearLimits"] = this.linearLimits?.mapKeys { (axis, pair) -> axis.name }?.mapValues { (axis, pair) -> pair.toLua() }
            constraint["twistLimit"] = this.twistLimit?.toLua()
            constraint["swingLimit"] = this.swingLimit?.toLua()
            constraint["pyramidSwingLimit"] = this.pyramidSwingLimit?.toLua()
            constraint["drives"] = this.drives?.mapKeys { (d, j) -> d.name }?.mapValues { (d, j) -> j.toLua() }
            constraint["drivePosition"] = this.drivePosition?.toLua()
            constraint["driveVelocity"] = this.driveVelocity?.toLua()
        }

        return constraint
    }

    fun Map<*, *>.toVector(): Vector3dc {
        val posTable = this as? Map<String, Double>
            ?: throw LuaException("Invalid Argument! Expects either a vector or a table with x, y, and z keys!")

        return Vector3d(posTable["x"] ?: 0.0, posTable["y"] ?: 0.0, posTable["z"] ?: 0.0)
    }
}
