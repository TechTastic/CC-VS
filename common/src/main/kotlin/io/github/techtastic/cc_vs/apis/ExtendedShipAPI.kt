package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.IComputerSystem
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.ship.PhysTickEventHandler
import io.github.techtastic.cc_vs.ship.PhysicsTicksEventHandler
import io.github.techtastic.cc_vs.ship.QueuedForcesApplier
import io.github.techtastic.cc_vs.util.CCVSUtils.toVector
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.vsCore

class ExtendedShipAPI(system: IComputerSystem) : ShipAPI(system) {
    @LuaFunction
    fun applyInvariantForce(args: IArguments) {
        val newForce =
            if (args.count() == 1)
                args.getTable(0).toVector()
            else
                Vector3d(args.getDouble(0), args.getDouble(1), args.getDouble(2))
        QueuedForcesApplier.getOrCreateControl(getShip()).applyInvariantForce(newForce)
    }

    @LuaFunction
    fun applyInvariantTorque(args: IArguments) {
        val newTorque =
            if (args.count() == 1)
                args.getTable(0).toVector()
            else
                Vector3d(args.getDouble(0), args.getDouble(1), args.getDouble(2))
        QueuedForcesApplier.getOrCreateControl(getShip()).applyInvariantTorque(newTorque)
    }

    @LuaFunction
    fun applyInvariantForceToPos(args: IArguments) {
        val (newForce, newPos) =
            if (args.count() == 2)
                Pair(args.getTable(0).toVector(), args.getTable(1).toVector())
            else
                Pair(
                    Vector3d(args.getDouble(0), args.getDouble(1), args.getDouble(2)),
                    Vector3d(args.getDouble(3), args.getDouble(4), args.getDouble(5))
                )
        QueuedForcesApplier.getOrCreateControl(getShip()).applyInvariantForceToPos(newForce, newPos)
    }

    @LuaFunction
    fun applyRotDependentForce(args: IArguments) {
        val newForce =
            if (args.count() == 1)
                args.getTable(0).toVector()
            else
                Vector3d(args.getDouble(0), args.getDouble(1), args.getDouble(2))
        QueuedForcesApplier.getOrCreateControl(getShip()).applyRotDependentForce(newForce)
    }

    @LuaFunction
    fun applyRotDependentTorque(args: IArguments) {
        val newTorque =
            if (args.count() == 1)
                args.getTable(0).toVector()
            else
                Vector3d(args.getDouble(0), args.getDouble(1), args.getDouble(2))
        QueuedForcesApplier.getOrCreateControl(getShip()).applyRotDependentTorque(newTorque)
    }

    @LuaFunction
    fun applyRotDependentForceToPos(args: IArguments) {
        val (newForce, newPos) =
            if (args.count() == 2)
                Pair(args.getTable(0).toVector(), args.getTable(1).toVector())
            else
                Pair(
                    Vector3d(args.getDouble(0), args.getDouble(1), args.getDouble(2)),
                    Vector3d(args.getDouble(3), args.getDouble(4), args.getDouble(5))
                )
        QueuedForcesApplier.getOrCreateControl(getShip()).applyRotDependentForceToPos(newForce, newPos)
    }

    @LuaFunction
    fun setStatic(b: Boolean) {
        QueuedForcesApplier.getOrCreateControl(getShip()).setStatic(b)
    }

    @LuaFunction
    fun setScale(scale: Double) {
        vsCore.scaleShip(system.level.shipObjectWorld, getShip(), scale)
    }

    @LuaFunction
    fun teleport(args: IArguments) {
        if (!PlatformUtils.canTeleport())
            throw LuaException("Teleporting is Disabled via CC: VS Config!")

        val input = args.getTable(0)

        var pos = getShip().transform.positionInWorld
        if (input.containsKey("pos"))
            pos = getVectorFromTable(input, "pos")

        var rot = getShip().transform.shipToWorldRotation
        if (input.containsKey("rot"))
            rot = getQuaternionFromTable(input).normalize(Quaterniond())

        var vel = getShip().velocity
        if (input.containsKey("vel"))
            vel = getVectorFromTable(input, "vel")

        var omega = getShip().omega
        if (input.containsKey("omega"))
            omega = getVectorFromTable(input, "omega")

        var dimension: String? = null
        if (input.containsKey("dimension"))
            dimension = (input["dimension"] ?: throwMalformedSectionError("dimension")) as String

        var scale = getShip().transform.shipToWorldScaling.x()
        if (input.containsKey("scale"))
            scale = (input["scale"] ?: throwMalformedSectionError("scale")) as Double

        val teleportData = ShipTeleportDataImpl(pos, rot, vel, omega, dimension, scale)

        println("Rot: ${teleportData.newRot}\n")

        //vsCore.teleportShip(this.level.shipObjectWorld, getShip(), teleportData)
        system.level.shipObjectWorld.teleportShip(getShip(), teleportData)
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