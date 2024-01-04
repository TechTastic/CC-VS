package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.core.apis.IAPIEnvironment
import io.github.techtastic.cc_vs.ship.QueuedForcesApplier
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.saveAttachment

class ExtendedShipAPI(environment: IAPIEnvironment, ship: ServerShip) : ShipAPI(environment, ship) {
    var control: QueuedForcesApplier = this.ship.getAttachment(QueuedForcesApplier::class.java) ?: 
    run {
        val newControl = QueuedForcesApplier()
        ship.saveAttachment(newControl)
        newControl
    }

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
}