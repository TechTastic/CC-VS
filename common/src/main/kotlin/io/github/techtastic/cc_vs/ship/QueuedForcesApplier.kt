package io.github.techtastic.cc_vs.ship

import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.ShipForcesInducer
import org.valkyrienskies.core.util.pollUntilEmpty
import java.util.concurrent.ConcurrentLinkedQueue

class QueuedForcesApplier: ShipForcesInducer {
    private val invForces: ConcurrentLinkedQueue<Vector3dc> = ConcurrentLinkedQueue()

    private val invPosForces: ConcurrentLinkedQueue<ForceAtPos> = ConcurrentLinkedQueue()

    private val invTorques: ConcurrentLinkedQueue<Vector3dc> = ConcurrentLinkedQueue()

    private val rotForces: ConcurrentLinkedQueue<Vector3dc> = ConcurrentLinkedQueue()

    private val rotPosForces: ConcurrentLinkedQueue<ForceAtPos> = ConcurrentLinkedQueue()

    private val rotTorques: ConcurrentLinkedQueue<Vector3dc> = ConcurrentLinkedQueue()

    @Volatile
    var toBeStatic = false

    @Volatile
    var toBeStaticUpdated = false

    override fun applyForces(physShip: PhysShip) {

        invForces.pollUntilEmpty(physShip::applyInvariantForce)
        invTorques.pollUntilEmpty(physShip::applyInvariantTorque)
        invPosForces.pollUntilEmpty { (force, pos) -> physShip.applyInvariantForceToPos(force, pos) }
        rotForces.pollUntilEmpty(physShip::applyRotDependentForce)
        rotTorques.pollUntilEmpty(physShip::applyRotDependentTorque)
        rotPosForces.pollUntilEmpty { (force, pos) -> physShip.applyRotDependentForceToPos(force, pos) }

        if (toBeStaticUpdated) {
            physShip.isStatic = toBeStatic
            toBeStaticUpdated = false
        }
    }

    fun applyInvariantForce(force: Vector3dc) {
        println("Force: $force")
        invForces.add(force)
    }

    fun applyInvariantTorque(torque: Vector3dc) {
        invTorques.add(torque)
    }

    fun applyInvariantForceToPos(force: Vector3dc, pos: Vector3dc) {
        invPosForces.add(ForceAtPos(force, pos))
    }

    fun applyRotDependentForce(force: Vector3dc) {
        rotForces.add(force)
    }

    fun applyRotDependentTorque(torque: Vector3dc) {
        rotTorques.add(torque)
    }

    fun applyRotDependentForceToPos(force: Vector3dc, pos: Vector3dc) {
        rotPosForces.add(ForceAtPos(force, pos))
    }

    fun setStatic(b: Boolean) {
        toBeStatic = b
        toBeStaticUpdated = true
    }

    companion object {
        fun getOrCreateControl(ship: ServerShip): QueuedForcesApplier {
            var control = ship.getAttachment(QueuedForcesApplier::class.java)
            if (control == null) {
                control = QueuedForcesApplier()
                ship.saveAttachment(QueuedForcesApplier::class.java, control)
            }

            return control
        }
    }

    private data class ForceAtPos(val force: Vector3dc, val pos: Vector3dc)
}