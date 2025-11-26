package io.github.techtastic.cc_vs

import dan200.computercraft.api.ComputerCraftAPI
import dan200.computercraft.shared.computer.core.ServerContext
import dev.architectury.event.events.common.LifecycleEvent
import io.github.techtastic.cc_vs.apis.ShipAPI
import io.github.techtastic.cc_vs.ship.PhysTickEventHandler
import io.github.techtastic.cc_vs.ship.PhysicsTicksEventHandler
import org.valkyrienskies.core.impl.hooks.VSEvents

object CCVSMod {
    const val MOD_ID = "cc_vs"
    lateinit var context: ServerContext

    @JvmStatic
    fun init() {
        LifecycleEvent.SERVER_STARTED.register { context = ServerContext.get(it) }

        ComputerCraftAPI.registerAPIFactory(::ShipAPI)

        VSEvents.shipLoadEvent.on { huh -> huh.ship.setAttachment(PhysTickEventHandler::class.java, null) }

        VSEvents.tickEndEvent.on { huh -> huh.world.loadedShips.forEach { ship ->
            PhysicsTicksEventHandler.getOrCreateControl(ship).resetData()
        } }
    }

    @JvmStatic
    fun initClient() {
    }
}
