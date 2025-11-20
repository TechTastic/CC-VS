package io.github.techtastic.cc_vs.util

import dan200.computercraft.api.lua.ILuaAPI
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.core.apis.IAPIEnvironment
import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.computer.core.ServerComputer
import io.github.techtastic.cc_vs.CCVSMod
import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.apis.ExtendedShipAPI
import io.github.techtastic.cc_vs.apis.ShipAPI
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import org.joml.*
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.apigame.constraints.*
import org.valkyrienskies.mod.common.getShipManagingPos

object CCVSUtils {
    /*fun applyShipAPIsToComputer(computer: ServerComputer, level: ServerLevel, ship: ServerShip?) {
        if (ship == null)
            return

        // Get the 'Computer' instance from 'computer' (which is an instance of ServerComputer)
        val trueComputer = computer::class.java
            .getDeclaredField("computer")
            .apply { isAccessible = true }
            .get(computer)

        val computerClass = trueComputer::class.java
        val apiEnvironment = computerClass
            .getDeclaredMethod("getAPIEnvironment")
            .apply { isAccessible = true }
            .invoke(trueComputer) as IAPIEnvironment

        val addApiMethod = computerClass
            .getDeclaredMethod("addApi", ILuaAPI::class.java)
            .apply { isAccessible = true }

        if (!PlatformUtils.isCommandOnly() || computer.family == ComputerFamily.COMMAND)
            addApiMethod.invoke(trueComputer, ExtendedShipAPI(apiEnvironment, ship, level))
        else
            addApiMethod.invoke(trueComputer, ShipAPI(ship, level))
    }*/

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

    fun VSConstraintAndId.toLua() = mapOf(Pair("id", this.constraintId), Pair("constraint", this.vsConstraint.toLua()))

    fun VSConstraint.toLua(): Map<String, Any> {
        val constraint = mutableMapOf<String, Any>()

        constraint["shipId0"] = this.shipId0
        constraint["shipId1"] = this.shipId1
        constraint["type"] = this.constraintType.name
        constraint["compliance"] = this.compliance

        if (this is VSForceConstraint) {
            constraint["localPos0"] = this.localPos0.toLua()
            constraint["localPos1"] = this.localPos1.toLua()
            constraint["maxForce"] = this.maxForce
        }

        if (this is VSTorqueConstraint) {
            constraint["localRot0"] = this.localRot0.toLua()
            constraint["localRot1"] = this.localRot1.toLua()
            constraint["maxTorque"] = this.maxTorque
        }

        when (this) {
            is VSAttachmentConstraint -> {
                constraint["fixedDistance"] = this.fixedDistance
            }

            is VSHingeSwingLimitsConstraint -> {
                constraint["minSwingAngle"] = this.minSwingAngle
                constraint["maxSwingAngle"] = this.maxSwingAngle
            }

            is VSHingeTargetAngleConstraint -> {
                constraint["targetAngle"] = this.targetAngle
                constraint["nextTickTargetAngle"] = this.nextTickTargetAngle
            }

            is VSPosDampingConstraint -> {
                constraint["posDamping"] = this.posDamping
            }

            is VSRopeConstraint -> {
                constraint["ropeLength"] = this.ropeLength
            }

            is VSRotDampingConstraint -> {
                constraint["rotDamping"] = this.rotDamping
                constraint["rotDampingAxes"] = this.rotDampingAxes.name
            }

            is VSSlideConstraint -> {
                constraint["localSlideAxis0"] = this.localSlideAxis0.toLua()
                constraint["maxDistBetweenPoints"] = this.maxDistBetweenPoints
            }

            is VSSphericalSwingLimitsConstraint -> {
                constraint["minSwingAngle"] = this.minSwingAngle
                constraint["maxSwingAngle"] = this.maxSwingAngle
            }

            is VSSphericalTwistLimitsConstraint -> {
                constraint["minTwistAngle"] = this.minTwistAngle
                constraint["maxTwistAngle"] = this.maxTwistAngle
            }
            else -> {}
        }

        return constraint
    }


    fun Map<*, *>.toVector(): Vector3dc {
        val posTable = this as? Map<String, Double>
            ?: throw LuaException("Invalid Argument! Expects either a vector or a table with x, y, and z keys!")

        return Vector3d(posTable["x"] ?: 0.0, posTable["y"] ?: 0.0, posTable["z"] ?: 0.0)
    }
}