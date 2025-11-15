package io.github.techtastic.cc_vs

import dan200.computercraft.api.ComputerCraftAPI
import dan200.computercraft.api.component.ComputerComponents
import dan200.computercraft.shared.computer.core.ServerContext
import dev.architectury.event.events.common.LifecycleEvent
import io.github.techtastic.cc_vs.apis.ExtendedShipAPI
import io.github.techtastic.cc_vs.apis.ShipAPI
import io.github.techtastic.cc_vs.ship.PhysTickEventHandler
import org.valkyrienskies.core.impl.hooks.VSEvents


object CCVSMod {
    const val MOD_ID = "cc_vs"
    lateinit var context: ServerContext

    @JvmStatic
    fun init() {
        LifecycleEvent.SERVER_STARTED.register { context = ServerContext.get(it) }

        ComputerCraftAPI.registerAPIFactory { system ->
            val admin = system.getComponent(ComputerComponents.ADMIN_COMPUTER)
            if (!PlatformUtils.isCommandOnly() || admin != null)
                return@registerAPIFactory ExtendedShipAPI(system)
            else
                return@registerAPIFactory ShipAPI(system)
        }

        VSEvents.shipLoadEvent.on { huh -> huh.ship.setAttachment(PhysTickEventHandler::class.java, null) }
    }

    @JvmStatic
    fun initClient() {
    }
}
