package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.ILuaAPI
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.core.apis.IAPIEnvironment
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector4d
import org.joml.primitives.AABBi
import org.valkyrienskies.core.api.ships.ServerShip
import kotlin.math.asin
import kotlin.math.atan2

open class ShipAPI(val environment: IAPIEnvironment, val ship: ServerShip) : ILuaAPI {
    var names: ArrayList<String> = arrayListOf("ship", this.ship.slug ?: "ship")

    override fun getNames(): Array<String> = names.toTypedArray()

    override fun update() {
        names[1] = this.ship.slug ?: "ship"

        super.update()
    }

    @LuaFunction
    fun getId(): Long = this.ship.id

    @LuaFunction
    fun getMass(): Double = this.ship.inertiaData.mass

    @LuaFunction
    fun getMomentOfInertiaTensor(): List<List<Double>> {
        val tensor: MutableList<List<Double>> = mutableListOf()

        for (i in 0..2) {
            val row = this.ship.inertiaData.momentOfInertiaTensor.getRow(i, Vector3d())
            tensor.add(i, listOf(row.x, row.y, row.z))
        }

        return tensor
    }

    @LuaFunction
    fun getName(): String = this.ship.slug ?: "no-name"

    @LuaFunction
    fun getOmega(): Map<String, Double> = vectorToTable(this.ship.omega)

    @LuaFunction
    fun getQuaternion(): Map<String, Double> {
        val q = this.ship.transform.shipToWorldRotation
        return mapOf(
                Pair("x", q.x()),
                Pair("y", q.y()),
                Pair("z", q.z()),
                Pair("w", q.w())
        )
    }

    @LuaFunction
    fun getRoll(): Double {
        val q = this.ship.transform.shipToWorldRotation
        return atan2(2.0 * (q.x() * q.y() + q.w() * q.z()), q.w() * q.w() + q.x() * q.x() - q.y() * q.y() - q.z() * q.z())
    }

    @LuaFunction
    fun getYaw(): Double {
        val q = this.ship.transform.shipToWorldRotation
        return asin(-2.0 * (q.x() * q.z() - q.w() * q.y()))
    }

    @LuaFunction
    fun getPitch(): Double {
        val q = this.ship.transform.shipToWorldRotation
        return atan2(2.0 * (q.y() * q.z() + q.w() * q.x()), q.w() * q.w() - q.x() * q.x() - q.y() * q.y() + q.z() * q.z())
    }

    @LuaFunction
    fun getScale(): Map<String, Double> = vectorToTable(this.ship.transform.shipToWorldScaling)

    @LuaFunction
    fun getShipyardPosition(): Map<String, Double> = vectorToTable(this.ship.transform.positionInShip)

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
    fun getVelocity(): Map<String, Double> = vectorToTable(this.ship.velocity)

    @LuaFunction
    fun getWorldspacePosition(): Map<String, Double> = vectorToTable(this.ship.transform.positionInWorld)

    @LuaFunction
    fun isStatic(): Boolean = this.ship.isStatic

    @LuaFunction
    fun setName(name: String) {
        this.ship.slug = name
    }

    @LuaFunction
    fun getRotationMatrix(): List<List<Double>> {
        val transform = this.ship.transform.shipToWorld
        val matrix: MutableList<List<Double>> = mutableListOf()

        for (i in 0..3) {
            val row = transform.getRow(i, Vector4d())
            matrix.add(i, listOf(row.x, row.y, row.z, row.w))
        }

        return matrix.toList()
    }

    private fun vectorToTable(vec: Vector3dc): Map<String, Double> = mapOf(
            Pair("x", vec.x()),
            Pair("y", vec.y()),
            Pair("z", vec.z())
    )
}