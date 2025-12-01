package io.github.techtastic.cc_vs.util

import dan200.computercraft.api.lua.LuaException
import io.github.techtastic.cc_vs.CCVSMod
import org.joml.*
import org.valkyrienskies.core.internal.joints.*

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

    fun getComputerByID(id: Int) = CCVSMod.context.registry().computers.find { computer -> computer.id == id }

    fun VSJointAndId.toLua() = mapOf(Pair("id", this.jointId), Pair("constraint", this.joint.toLua()))

    fun VSJointPose.toLua() = mapOf("pos" to this.pos.toLua(), "rot" to this.rot.toLua())

    fun VSD6Joint.LinearLimitPair.toLua() = mapOf(
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
            TODO("VSRevoluteJoint to Lua")
        } else if (this is VSGearJoint) {
            TODO("VSGearJoint to Lua")
        } else if (this is VSRackAndPinionJoint) {
            TODO("VSRackAndPinionJoint to Lua")
        } else if (this is VSD6Joint) {
            TODO("VSD6Joint to Lua")
        }

        return constraint
    }


    fun Map<*, *>.toVector(): Vector3dc {
        val posTable = this as? Map<String, Double>
            ?: throw LuaException("Invalid Argument! Expects either a vector or a table with x, y, and z keys!")

        return Vector3d(posTable["x"] ?: 0.0, posTable["y"] ?: 0.0, posTable["z"] ?: 0.0)
    }
}