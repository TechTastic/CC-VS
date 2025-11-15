package io.github.techtastic.cc_vs.ship

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.apis.LuaPhysShip
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import java.util.concurrent.ConcurrentLinkedQueue

@Deprecated("Simply here so I can remove it from older Ships")
class PhysTickEventHandler: ShipForcesInducer, ServerTickListener {
    @JsonIgnore
    private val computers = mutableListOf<Int>()
    private val queuedData = ConcurrentLinkedQueue<LuaPhysShip>()

    override fun applyForces(physShip: PhysShip) {}

    fun addComputer(id: Int) {}

    override fun onServerTick() {}

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