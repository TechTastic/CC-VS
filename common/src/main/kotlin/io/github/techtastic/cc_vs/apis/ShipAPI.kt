package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.ILuaAPI
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import io.github.techtastic.cc_vs.mixin.ShipObjectWorldAccessor
import io.github.techtastic.cc_vs.util.CCVSUtils
import io.github.techtastic.cc_vs.util.CCVSUtils.toLua
import io.github.techtastic.cc_vs.util.CCVSUtils.toVector
import net.fabricmc.loader.impl.lib.sat4j.core.Vec
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector4d
import org.joml.primitives.AABBi
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import kotlin.math.asin
import kotlin.math.atan2

open class ShipAPI(val ship: ServerShip, val level: ServerLevel) : ILuaAPI {
    var names: ArrayList<String> = arrayListOf("ship", this.ship.slug ?: "ship")

    override fun getNames(): Array<String> = names.toTypedArray()

    override fun update() {
        names[1] = this.ship.slug ?: "ship"

        super.update()
    }

    @LuaFunction
    fun getId(): Long =
        this.ship.id

    @LuaFunction
    fun getMass(): Double =
        this.ship.inertiaData.mass

    @LuaFunction
    fun getMomentOfInertiaTensorToSave(): List<List<Double>> =
        this.ship.inertiaData.momentOfInertiaTensorToSave.toLua()

    @LuaFunction
    fun getMomentOfInertiaTensor(): List<List<Double>> =
        this.ship.inertiaData.momentOfInertiaTensor.toLua()

    @LuaFunction
    fun getSlug(): String = this.ship.slug ?: "no-name"

    @LuaFunction
    fun getOmega(): Map<String, Double> =
        this.ship.omega.toLua()

    @LuaFunction
    fun getQuaternion(): Map<String, Double> =
        this.ship.transform.shipToWorldRotation.toLua()

    @LuaFunction
    fun getScale(): Map<String, Double> =
        this.ship.transform.shipToWorldScaling.toLua()

    @LuaFunction
    fun getShipyardPosition(): Map<String, Double> =
        this.ship.transform.positionInShip.toLua()

    @LuaFunction
    fun getSize(): Map<String, Any> {
        val aabb = this.ship.shipAABB ?: AABBi(0, 0, 0, 0, 0, 0)
        return mapOf(
            Pair("x", aabb.maxX() - aabb.minX()),
            Pair("y", aabb.maxY() - aabb.minY()),
            Pair("z", aabb.maxZ() - aabb.minZ())
        )
    }

    @LuaFunction
    fun getVelocity(): Map<String, Double> =
        this.ship.velocity.toLua()

    @LuaFunction
    fun getWorldspacePosition(): Map<String, Double> =
        this.ship.transform.positionInWorld.toLua()

    @LuaFunction
    fun transformPositionToWorld(args: IArguments): Map<String, Double> {
        val pos =
            if (args.count() == 1)
                Vector3d(args.getTable(0).toVector())
            else
                Vector3d(args.getDouble(0), args.getDouble(1), args.getDouble(2))
        return this.ship.shipToWorld.transformPosition(pos).toLua()
    }

    @LuaFunction
    fun isStatic(): Boolean = this.ship.isStatic

    @LuaFunction
    fun setSlug(name: String) {
        this.ship.slug = name
    }

    @LuaFunction
    fun getTransformationMatrix(): List<List<Double>> {
        val transform = this.ship.transform.shipToWorld
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
        return accessor.shipIdToConstraints.getOrDefault(ship.id, setOf()).map { id ->
            accessor.constraints[id]?.let { VSConstraintAndId(id, it) }
        }.map { combo -> combo?.toLua() }
    }
}