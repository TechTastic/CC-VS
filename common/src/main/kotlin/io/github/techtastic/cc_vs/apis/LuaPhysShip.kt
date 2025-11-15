package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.LuaFunction
import io.github.techtastic.cc_vs.util.CCVSUtils.toLua
import org.valkyrienskies.core.api.VSBeta
import org.valkyrienskies.core.api.ships.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysInertia
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl

data class LuaPhysShip(
    private val buoyantFactor: Double,
    private val static: Boolean,
    private val fluidDrag: Boolean,
    private val inertia: Map<String, Any>,
    private val poseVel: Map<String, Map<String, Double>>,
    private val forceInducers: List<String>
) {
    @OptIn(VSBeta::class)
    constructor(physShip: PhysShipImpl): this(
        physShip.buoyantFactor, physShip.isStatic, physShip.doFluidDrag,
        physShip.inertia.let { inertia ->
            mapOf(
                Pair("momentOfInertiaTensor", inertia.momentOfInertiaTensor.toLua()),
                Pair("mass", inertia.shipMass)
            )
        },
        physShip.poseVel.let { poseVel ->
            mapOf(
                Pair("vel", poseVel.vel.toLua()),
                Pair("omega", poseVel.omega.toLua()),
                Pair("pos", poseVel.pos.toLua()),
                Pair("rot", poseVel.rot.toLua())
            )
        },
        physShip.forceInducers.map(ShipForcesInducer::toString)
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