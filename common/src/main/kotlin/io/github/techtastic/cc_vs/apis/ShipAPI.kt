package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.ILuaAPI
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.shared.computer.blocks.TileComputer
import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.computer.core.ServerComputer
import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.mixin.ShipObjectWorldAccessor
import io.github.techtastic.cc_vs.ship.PhysicsTicksEventHandler
import io.github.techtastic.cc_vs.ship.QueuedForcesApplier
import io.github.techtastic.cc_vs.util.CCVSUtils.toLua
import io.github.techtastic.cc_vs.util.CCVSUtils.toVector
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import org.joml.*
import org.joml.primitives.AABBi
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.vsCore
import java.util.*

open class ShipAPI(val level: ServerLevel, val pos: BlockPos) : ILuaAPI {
    private fun verifyAdmin() {
        var isCommand = false
        val be = level.getBlockEntity(pos) as? TileComputer
        try {
            // Its being weird about Fabric stuff
            Objects.requireNonNull(be)
            val clazz = TileComputer::class.java
            val method = clazz.getMethod("getFamily")
            isCommand = method.invoke(be) == ComputerFamily.COMMAND
        } catch (_: Exception) {}
        if (PlatformUtils.isCommandOnly() && isCommand)
            throw LuaException("This method requires a Command Computer!")
    }

    override fun startup() {
        try {
            if (PlatformUtils.exposePhysTick())
                PhysicsTicksEventHandler.getOrCreateControl(getShip())
        } catch (_: LuaException) {}
        super.startup()
    }

    override fun update() {
        try {
            if (PlatformUtils.exposePhysTick()) {
                val data = PhysicsTicksEventHandler.getOrCreateControl(getShip()).getData()
                val clazz = TileComputer::class.java
                val method = clazz.getMethod("getServerComputer")
                val obj = method.invoke(level.getBlockEntity(pos))
                (obj as? ServerComputer)?.queueEvent("physics_ticks", data)
            }
        } catch (_: LuaException) {}
        super.update()
    }

    override fun shutdown() {
        try {
            if (PlatformUtils.exposePhysTick())
                PhysicsTicksEventHandler.getOrCreateControl(getShip())
        } catch (_: LuaException) {}
        super.shutdown()
    }

    override fun getNames(): Array<out String>? = arrayOf("ship")

    protected fun getShip(): LoadedServerShip {
        return level.getShipObjectManagingPos(pos)
            ?: throw LuaException("This computer is not on a Ship!")
    }

    @LuaFunction
    fun pullPhysicsTicks(): Array<Any>? {
        if (!PlatformUtils.exposePhysTick())
            throw LuaException("Physics Tick is not exposed! This is a configuration option!")
        return null
    }

    @LuaFunction
    fun getId(): Long =
        getShip().id

    @LuaFunction
    fun getMass(): Double =
        getShip().inertiaData.mass

    @LuaFunction
    fun getMomentOfInertiaTensorToSave(): List<List<Double>> =
        getShip().inertiaData.momentOfInertiaTensorToSave.toLua()

    @LuaFunction
    fun getMomentOfInertiaTensor(): List<List<Double>> =
        getShip().inertiaData.momentOfInertiaTensor.toLua()

    @LuaFunction
    fun getSlug(): String = getShip().slug ?: "no-name"

    @LuaFunction
    fun getOmega(): Map<String, Double> =
        getShip().omega.toLua()

    @LuaFunction
    fun getQuaternion(): Map<String, Double> =
        getShip().transform.shipToWorldRotation.toLua()

    @LuaFunction
    fun getScale(): Map<String, Double> =
        getShip().transform.shipToWorldScaling.toLua()

    @LuaFunction
    fun getShipyardPosition(): Map<String, Double> =
        getShip().transform.positionInShip.toLua()

    @LuaFunction
    fun getSize(): Map<String, Any> {
        val aabb = getShip().shipAABB ?: AABBi(0, 0, 0, 0, 0, 0)
        return mapOf(
            Pair("x", aabb.maxX() - aabb.minX()),
            Pair("y", aabb.maxY() - aabb.minY()),
            Pair("z", aabb.maxZ() - aabb.minZ())
        )
    }

    @LuaFunction
    fun getVelocity(): Map<String, Double> =
        getShip().velocity.toLua()

    @LuaFunction
    fun getWorldspacePosition(): Map<String, Double> =
        getShip().transform.positionInWorld.toLua()

    @LuaFunction
    fun transformPositionToWorld(args: IArguments): Map<String, Double> {
        val pos =
            if (args.count() == 1)
                Vector3d(args.getTable(0).toVector())
            else
                Vector3d(args.getDouble(0), args.getDouble(1), args.getDouble(2))
        return getShip().shipToWorld.transformPosition(pos).toLua()
    }

    @LuaFunction
    fun isStatic(): Boolean = getShip().isStatic

    @LuaFunction
    fun setSlug(name: String) {
        getShip().slug = name
    }

    @LuaFunction
    fun getTransformationMatrix(): List<List<Double>> {
        val transform = getShip().transform.shipToWorld
        val matrix: MutableList<List<Double>> = mutableListOf()

        for (i in 0..3) {
            val row = transform.getRow(i, Vector4d())
            matrix.add(i, listOf(row.x, row.y, row.z, row.w))
        }

        return matrix.toList()
    }

    @LuaFunction
    fun getConstraints(): List<*> {
        val accessor = level.shipObjectWorld as ShipObjectWorldAccessor
        return accessor.shipIdToConstraints.getOrDefault(getShip().id, setOf()).map { id ->
            accessor.constraints[id]?.let { VSConstraintAndId(id, it) }
        }.map { combo -> combo?.toLua() }
    }

    @LuaFunction
    fun applyInvariantForce(forceX: Double, forceY: Double, forceZ: Double) {
        verifyAdmin()
        QueuedForcesApplier.getOrCreateControl(getShip()).applyInvariantForce(Vector3d(forceX, forceY, forceZ))
    }

    @LuaFunction
    fun applyInvariantTorque(torqueX: Double, torqueY: Double, torqueZ: Double) {
        verifyAdmin()
        QueuedForcesApplier.getOrCreateControl(getShip()).applyInvariantTorque(Vector3d(torqueX, torqueY, torqueZ))
    }

    @LuaFunction
    fun applyInvariantForceToPos(forceX: Double, forceY: Double, forceZ: Double, posX: Double, posY: Double, posZ: Double) {
        verifyAdmin()
        QueuedForcesApplier.getOrCreateControl(getShip()).applyInvariantForceToPos(Vector3d(forceX, forceY, forceZ), Vector3d(posX, posY, posZ))
    }

    @LuaFunction
    fun applyRotDependentForce(forceX: Double, forceY: Double, forceZ: Double) {
        verifyAdmin()
        QueuedForcesApplier.getOrCreateControl(getShip()).applyRotDependentForce(Vector3d(forceX, forceY, forceZ))
    }

    @LuaFunction
    fun applyRotDependentTorque(torqueX: Double, torqueY: Double, torqueZ: Double) {
        verifyAdmin()
        QueuedForcesApplier.getOrCreateControl(getShip()).applyRotDependentTorque(Vector3d(torqueX, torqueY, torqueZ))
    }

    @LuaFunction
    fun applyRotDependentForceToPos(forceX: Double, forceY: Double, forceZ: Double, posX: Double, posY: Double, posZ: Double) {
        verifyAdmin()
        QueuedForcesApplier.getOrCreateControl(getShip()).applyRotDependentForceToPos(Vector3d(forceX, forceY, forceZ), Vector3d(posX, posY, posZ))
    }

    @LuaFunction
    fun setStatic(b: Boolean) {
        QueuedForcesApplier.getOrCreateControl(getShip()).setStatic(b)
    }

    @LuaFunction
    fun setScale(scale: Double) {
        vsCore.scaleShip(level.shipObjectWorld, getShip(), scale)
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
        level.shipObjectWorld.teleportShip(getShip(), teleportData)
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