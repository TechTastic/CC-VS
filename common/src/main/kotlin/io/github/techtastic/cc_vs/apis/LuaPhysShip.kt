package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.LuaFunction
import io.github.techtastic.cc_vs.util.CCVSUtils.toLua
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.util.PhysTickOnly

data class LuaPhysShip(
    private val buoyantFactor: Double,
    private val static: Boolean,
    private val fluidDrag: Boolean,
    private val inertia: Map<String, Any>,
    private val poseVel: Map<String, Map<String, Double>>,
    private val forceInducers: List<String>
) {
    @OptIn(PhysTickOnly::class)
    constructor(physShip: PhysShip): this(
        physShip.buoyantFactor, physShip.isStatic, physShip.doFluidDrag,
        mapOf(
            Pair("momentOfInertia", physShip.momentOfInertia.toLua()),
            Pair("mass", physShip.mass)
        ),
        mapOf(
            Pair("vel", physShip.velocity.toLua()),
            Pair("omega", physShip.angularVelocity.toLua()),
            Pair("pos", physShip.centerOfMass.toLua()),
            Pair("rot", physShip.transform.shipToWorldRotation.toLua())
        ),
        listOf()
    )

    @LuaFunction
    fun getBuoyantFactor() = this.buoyantFactor

    @LuaFunction
    fun isStatic() = this.static

    @LuaFunction
    fun doFluidDrag() = this.fluidDrag

    @LuaFunction
    fun getInertia() = this.inertia

    @LuaFunction
    fun getPoseVel() = this.poseVel

    @LuaFunction
    fun getForcesInducers() = this.forceInducers
}