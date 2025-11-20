package io.github.techtastic.cc_vs

import dan200.computercraft.api.ComputerCraftAPI
import io.github.techtastic.cc_vs.apis.ShipAPI
import io.github.techtastic.cc_vs.ship.PhysTickEventHandler
import org.valkyrienskies.core.impl.hooks.VSEvents

object CCVSMod {
    const val MOD_ID = "cc_vs"

    @JvmStatic
    fun init() {
        VSEvents.shipLoadEvent.on { huh -> huh.ship.setAttachment(PhysTickEventHandler::class.java, null) }
    }

    @JvmStatic
    fun initClient() {
    }
}
