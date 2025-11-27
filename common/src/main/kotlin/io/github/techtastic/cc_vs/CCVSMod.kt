package io.github.techtastic.cc_vs

import io.github.techtastic.cc_vs.ship.PhysTickEventHandler
import io.github.techtastic.cc_vs.ship.PhysicsTicksEventHandler
import org.valkyrienskies.core.impl.hooks.VSEvents

object CCVSMod {
    const val MOD_ID = "cc_vs"

    @JvmStatic
    fun init() {
        VSEvents.shipLoadEvent.on { huh -> huh.ship.setAttachment(PhysTickEventHandler::class.java, null) }

        VSEvents.tickEndEvent.on { huh -> huh.world.loadedShips.forEach { ship ->
            PhysicsTicksEventHandler.getOrCreateControl(ship).resetData()
        } }
    }

    @JvmStatic
    fun initClient() {
    }
}
