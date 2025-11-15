package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.IComputerSystem
import dan200.computercraft.api.lua.ILuaAPI
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.mixin.ShipObjectWorldAccessor
import io.github.techtastic.cc_vs.ship.PhysicsTicksEventHandler
import io.github.techtastic.cc_vs.util.CCVSUtils
import io.github.techtastic.cc_vs.util.CCVSUtils.toLua
import io.github.techtastic.cc_vs.util.CCVSUtils.toVector
import net.fabricmc.loader.impl.lib.sat4j.core.Vec
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector4d
import org.joml.primitives.AABBi
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId
import org.valkyrienskies.core.game.ships.ShipObjectServer
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import kotlin.math.asin
import kotlin.math.atan2

open class ShipAPI(val system: IComputerSystem) : ILuaAPI {
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
                system.queueEvent("physics_ticks", *data)
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
        return system.level.getShipObjectManagingPos(system.position)
            ?: throw LuaException("This computer is not on a Ship!")
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
        val accessor = system.level.shipObjectWorld as ShipObjectWorldAccessor
        return accessor.shipIdToConstraints.getOrDefault(getShip().id, setOf()).map { id ->
            accessor.constraints[id]?.let { VSConstraintAndId(id, it) }
        }.map { combo -> combo?.toLua() }
    }
}