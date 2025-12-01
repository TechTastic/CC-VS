package io.github.techtastic.cc_vs

import dan200.computercraft.api.ComputerCraftAPI
import dan200.computercraft.shared.computer.core.ServerContext
import dev.architectury.event.events.common.LifecycleEvent
import io.github.techtastic.cc_vs.apis.ShipAPI

object CCVSMod {
    const val MOD_ID = "cc_vs"
    lateinit var context: ServerContext

    @JvmStatic
    fun init() {
        LifecycleEvent.SERVER_STARTED.register { context = ServerContext.get(it) }

        ComputerCraftAPI.registerAPIFactory(::ShipAPI)
    }

    @JvmStatic
    fun initClient() {
    }
}
