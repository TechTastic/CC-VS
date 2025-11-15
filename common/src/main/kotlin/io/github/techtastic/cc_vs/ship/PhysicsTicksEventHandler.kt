package io.github.techtastic.cc_vs.ship

import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.apis.LuaPhysShip
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import java.util.concurrent.ConcurrentLinkedQueue

class PhysicsTicksEventHandler: ShipForcesInducer {
    private val queuedData = ConcurrentLinkedQueue<LuaPhysShip>()

    override fun applyForces(physShip: PhysShip) {
        if (!PlatformUtils.exposePhysTick())
            return

        this.queuedData.add(LuaPhysShip(physShip as PhysShipImpl))
    }

    fun getData(): Array<LuaPhysShip> {
        val data = this.queuedData.toTypedArray()
        this.queuedData.clear()
        return data
    }

    companion object {
        fun getOrCreateControl(ship: ServerShip): PhysicsTicksEventHandler {
            var control = ship.getAttachment<PhysicsTicksEventHandler>()
            if (control == null) {
                control = PhysicsTicksEventHandler()
                ship.saveAttachment<PhysicsTicksEventHandler>(control)
            }

            return control
        }
    }
}