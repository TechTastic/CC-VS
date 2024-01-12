package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.core.apis.IAPIEnvironment
import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.ship.QueuedForcesApplier
import net.minecraft.server.level.ServerLevel
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.vsCore

class ExtendedShipAPI(environment: IAPIEnvironment, ship: ServerShip, val level: ServerLevel) : ShipAPI(environment, ship) {
    var control: QueuedForcesApplier = QueuedForcesApplier.getOrCreateControl(this.ship)

    @LuaFunction
    fun applyInvariantForce(xForce: Double, yForce: Double, zForce: Double) {
        this.control.applyInvariantForce(Vector3d(xForce, yForce, zForce))
    }

    @LuaFunction
    fun applyInvariantTorque(xTorque: Double, yTorque: Double, zTorque: Double) {
        this.control.applyInvariantTorque(Vector3d(xTorque, yTorque, zTorque))
    }

    @LuaFunction
    fun applyInvariantForceToPos(xForce: Double, yForce: Double, zForce: Double, xPos: Double, yPos: Double, zPos: Double) {
        this.control.applyInvariantForceToPos(Vector3d(xForce, yForce, zForce), Vector3d(xPos, yPos, zPos))
    }

    @LuaFunction
    fun applyRotDependentForce(xForce: Double, yForce: Double, zForce: Double) {
        this.control.applyRotDependentForce(Vector3d(xForce, yForce, zForce))
    }

    @LuaFunction
    fun applyRotDependentTorque(xTorque: Double, yTorque: Double, zTorque: Double) {
        this.control.applyRotDependentTorque(Vector3d(xTorque, yTorque, zTorque))
    }

    @LuaFunction
    fun applyRotDependentForceToPos(xForce: Double, yForce: Double, zForce: Double, xPos: Double, yPos: Double, zPos: Double) {
        this.control.applyRotDependentForceToPos(Vector3d(xForce, yForce, zForce), Vector3d(xPos, yPos, zPos))
    }

    @LuaFunction
    fun setStatic(b: Boolean) {
        this.control.setStatic(b)
    }

    @LuaFunction
    fun setScale(scale: Double) {
        vsCore.scaleShip(level.shipObjectWorld, ship, scale)
    }

    @LuaFunction
    fun teleport(args: IArguments) {
        if (!PlatformUtils.canTeleport())
            throw LuaException("Teleporting is Disabled via CC: VS Config!")

        val input = args.getTable(0)
        
        var pos = this.ship.transform.positionInWorld
        if (input.containsKey("pos"))
            pos = getVectorFromTable(input, "pos")
        
        var rot = this.ship.transform.shipToWorldRotation
        if (input.containsKey("rot"))
            rot = getQuaternionFromTable(input).normalize(Quaterniond())

        var vel = this.ship.velocity
        if (input.containsKey("vel"))
            vel = getVectorFromTable(input, "vel")

        var omega = this.ship.omega
        if (input.containsKey("omega"))
            omega = getVectorFromTable(input, "omega")

        var dimension: String? = null
        if (input.containsKey("dimension"))
            dimension = (input["dimension"] ?: throwMalformedSectionError("dimension")) as String

        var scale = this.ship.transform.shipToWorldScaling.x()
        if (input.containsKey("scale"))
            scale = (input["scale"] ?: throwMalformedSectionError("scale")) as Double

        val teleportData = ShipTeleportDataImpl(pos, rot, vel, omega, dimension, scale)

        println("Rot: ${teleportData.newRot}\n")

        //vsCore.teleportShip(this.level.shipObjectWorld, this.ship, teleportData)
        this.level.shipObjectWorld.teleportShip(this.ship, teleportData)
    }

    private fun getVectorFromTable(input: Map<*, *>, section: String): Vector3dc {
        val table = (input[section] ?: throwMalformedSectionError(section)) as Map<*, *>
        return Vector3d(
                (table["x"] ?: throwMalformedFieldError(section, "x")) as Double,
                (table["y"] ?: throwMalformedFieldError(section, "y")) as Double,
                (table["z"] ?: throwMalformedFieldError(section, "z")) as Double
        )
    }

    private fun getQuaternionFromTable(input: Map<*, *>): Quaterniondc {
        val table = (input["rot"] ?: throwMalformedSectionError("rot")) as Map<*, *>
        return Quaterniond(
                (table["x"] ?: throwMalformedFieldError("rot", "x")) as Double,
                (table["y"] ?: throwMalformedFieldError("rot", "y")) as Double,
                (table["z"] ?: throwMalformedFieldError("rot", "z")) as Double,
                (table["w"] ?: throwMalformedFieldError("rot", "w")) as Double
        )
    }

    private fun throwMalformedSectionError(section: String): Nothing =
            throw LuaException("Malformed $section")
    private fun throwMalformedFieldError(section: String, field: String): Nothing =
            throw LuaException("Malformed $field key of $section")
}