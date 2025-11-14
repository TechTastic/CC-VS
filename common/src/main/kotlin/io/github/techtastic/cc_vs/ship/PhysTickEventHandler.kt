package io.github.techtastic.cc_vs.ship

import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.apis.LuaPhysShip
import org.valkyrienskies.core.api.ships.*
import org.joml.Vector3d
import io.github.techtastic.cc_vs.util.CCVSUtils.toLua
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import java.util.concurrent.ConcurrentLinkedQueue
import com.fasterxml.jackson.annotation.JsonIgnore

class PhysTickEventHandler : ShipForcesInducer {
    @JsonIgnore
    private val queuedData = ConcurrentLinkedQueue<LuaPhysShip>()

    // --- State ---
    @JsonIgnore private var initialized = false
    @JsonIgnore private val prevVel = Vector3d()
    @JsonIgnore private val prevOmega = Vector3d()

    // smoothed velocities (to suppress jitter)
    @JsonIgnore private val smoothVel = Vector3d()
    @JsonIgnore private val smoothOmega = Vector3d()

    // filtered accelerations (final output)
    @JsonIgnore private val smoothAcc = Vector3d()
    @JsonIgnore private val smoothRotAcc = Vector3d()

    // --- Filter constants ---
    @JsonIgnore private val physDt = 1.0 / 60.0 // physics tick interval (s)

    // velocity smoothing factor (0.3–0.5 typical)
    @JsonIgnore private val beta = 0.5

    // acceleration smoothing time constant and factor
    @JsonIgnore private val tau = 0.1
    @JsonIgnore private val alpha = physDt / (tau + physDt)

    override fun applyForces(physShip: PhysShip) {
        val ship = physShip as PhysShipImpl
        val vel = Vector3d(ship.poseVel.vel)
        val omega = Vector3d(ship.poseVel.omega)

        if (!initialized) {
            // initialize smooth and previous values
            prevVel.set(vel)
            prevOmega.set(omega)
            smoothVel.set(vel)
            smoothOmega.set(omega)
            initialized = true
        }

        // --- Step 1: Exponential smoothing on velocity (reduce jitter) ---
        smoothVel.fma(beta, Vector3d(vel).sub(smoothVel), smoothVel)
        smoothOmega.fma(beta, Vector3d(omega).sub(smoothOmega), smoothOmega)

        // --- Step 2: Differentiate smoothed velocity ---
        val rawAcc = Vector3d(smoothVel).sub(prevVel).div(physDt)
        val rawRotAcc = Vector3d(smoothOmega).sub(prevOmega).div(physDt)

        // --- Step 3: IIR low-pass on acceleration ---
        smoothAcc.lerp(rawAcc, alpha)
        smoothRotAcc.lerp(rawRotAcc, alpha)

        // --- Update previous smoothed velocities ---
        prevVel.set(smoothVel)
        prevOmega.set(smoothOmega)


        if (!PlatformUtils.exposePhysTick())
            return

        this.queuedData.add(LuaPhysShip(ship))
    }

    @JsonIgnore
    fun getData(): Array<LuaPhysShip> {
        val data = this.queuedData.toTypedArray()
        this.queuedData.clear()
        return data
    }

    @JsonIgnore
    fun getAcceleration(): Map<String, Double> = smoothAcc.toLua()

    @JsonIgnore
    fun getRotationalAcceleration(): Map<String, Double> = smoothRotAcc.toLua()

    companion object {
        fun getOrCreateControl(ship: ServerShip): PhysTickEventHandler {
            var control = ship.getAttachment<PhysTickEventHandler>()
            if (control == null) {
                control = PhysTickEventHandler()
                ship.saveAttachment<PhysTickEventHandler>(control)
            }

            return control
        }
    }
}