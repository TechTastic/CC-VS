package io.github.techtastic.cc_vs.ship

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.apis.LuaPhysShip
import io.github.techtastic.cc_vs.util.CCVSUtils
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import java.util.concurrent.ConcurrentLinkedQueue

class PhysTickEventHandler: ShipForcesInducer, ServerTickListener {
    @JsonIgnore
    private val computers = mutableListOf<Int>()
    private val queuedData = ConcurrentLinkedQueue<LuaPhysShip>()

    override fun applyForces(physShip: PhysShip) {
        if (!PlatformUtils.exposePhysTick())
            return

        this.queuedData.add(LuaPhysShip(physShip as PhysShipImpl))
    }

    fun addComputer(id: Int) {
        this.computers.add(id)
    }

    override fun onServerTick() {
        this.computers.removeIf { CCVSUtils.getComputerByID(it) == null }
        this.computers.forEach {
            CCVSUtils.getComputerByID(it)?.queueEvent("physics_ticks", this.queuedData.toTypedArray())
        }
        this.queuedData.clear()
    }

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